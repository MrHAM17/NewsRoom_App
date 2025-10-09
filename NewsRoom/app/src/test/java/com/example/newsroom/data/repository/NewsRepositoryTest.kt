package com.example.newsroom.data.repository

import android.content.Context
import com.example.newsroom.core.util.network.NetworkStatus
import com.example.newsroom.core.util.network.getNetworkStatus
import com.example.newsroom.data.local.room.NewsDatabase
import com.example.newsroom.data.local.room.dao.BookmarkDao
import com.example.newsroom.data.local.room.dao.NewsSyncAndSearchDao
import com.example.newsroom.data.local.room.entity.BookmarkEntity
import com.example.newsroom.data.local.room.entity.FtsRow
import com.example.newsroom.data.local.room.entity.NewsEntity
import com.example.newsroom.data.local.room.entity.NewsFtsEntity
import com.example.newsroom.data.local.room.entity.SearchHistoryEntity
import com.example.newsroom.data.remote.api.NewsApiService
import com.example.newsroom.data.remote.model.TopHeadlinesResponse
import com.example.newsroom.data.remote.model.dto.Article
import com.example.newsroom.data.remote.model.dto.ArticleDto
import com.example.newsroom.data.remote.model.dto.SourceNameDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class NewsRepositoryUnitTest {

    private lateinit var repository: NewsRepository
    private lateinit var mockApi: NewsApiService
    private lateinit var mockDb: NewsDatabase
    private lateinit var mockContext: Context
    private lateinit var mockNewsSyncAndSearchDao: NewsSyncAndSearchDao
    private lateinit var mockBookmarkDao: BookmarkDao

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        mockApi = mockk()
        mockDb = mockk()
        mockContext = mockk()
        mockNewsSyncAndSearchDao = mockk()
        mockBookmarkDao = mockk()

        every { mockDb.newsDao() } returns mockNewsSyncAndSearchDao
        every { mockDb.bookmarkDao() } returns mockBookmarkDao

        repository = NewsRepository(mockApi, mockDb)

        // Mock network status utility
        mockkStatic("com.example.newsroom.core.util.network.NetworkUtilsKt")
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `safeNetworkCall returns failure when no network connection`() = runBlocking {
        // Given
        coEvery { getNetworkStatus(any()) } returns NetworkStatus(
            wifi = false,
            mobile = false,
            ethernet = false,
            internet = false
        )

        // When
        val result = repository.safeNetworkCall(mockContext) { "Success" }

        // Then
        assertTrue(result.isFailure)
        assertEquals("No network connection.. & no cached data is available to show here.", result.exceptionOrNull()?.message)
    }

    @Test
    fun `safeNetworkCall returns failure when no internet`() = runBlocking {
        // Given
        coEvery { getNetworkStatus(any()) } returns NetworkStatus(
            wifi = true,
            mobile = true,
            ethernet = true,
            internet = false
        )

        // When
        val result = repository.safeNetworkCall(mockContext) { "Success" }

        // Then
        assertTrue(result.isFailure)
        assertEquals("No internet.. & no cached data is available to show here.", result.exceptionOrNull()?.message)
    }

    @Test
    fun `safeNetworkCall returns success when network is available`() = runBlocking {
        // Given
        coEvery { getNetworkStatus(any()) } returns NetworkStatus(
            wifi = true,
            mobile = false,
            ethernet = false,
            internet = true
        )

        // When
        val result = repository.safeNetworkCall(mockContext) { "Success" }

        // Then
        assertTrue(result.isSuccess)
        assertEquals("Success", result.getOrNull())
    }

    @Test
    fun `topHeadlines emits success with articles on successful API call`() = runBlocking {
        // Given
        val mockArticleDto = ArticleDto(
            source = SourceNameDto("test-source-id", "Test Source"),
            author = "Test Author",
            title = "Test Title 1",
            description = "Test Description",
            url = "http://test.com/article1",
            urlToImage = "http://test.com/image1.jpg",
            publishedAt = "2023-01-01",
            content = "Test Content"
        )

        val mockResponse = TopHeadlinesResponse(
            status = "ok",
            totalResults = 1,
            articles = listOf(mockArticleDto)
        )

        coEvery { mockApi.getTopHeadlines(any(), any(), any(), any()) } returns mockResponse

        // When
        val result = repository.topHeadlines("general").first()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("Test Title 1", result.getOrNull()?.get(0)?.title)
    }

    @Test
    fun `topHeadlines emits failure on API exception`() = runBlocking {
        // Given
        coEvery { mockApi.getTopHeadlines(any(), any(), any(), any()) } throws Exception("API Error")

        // When
        val result = repository.topHeadlines("general").first()

        // Then
        assertTrue(result.isFailure)
        assertEquals("API Error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `saveBookmark upserts bookmark entity`() = runBlocking {
        // Given
        val article = Article(
            id = "1",
            sourceId = null,
            sourceName = "Test Source",
            author = "Test Author",
            title = "Test Title",
            description = "Test Description",
            url = "http://test.com/article",
            imageUrl = "http://test.com/image.jpg",
            publishedAtUtc = "2023-01-01",
            content = "Test Content"
        )

        coEvery { mockBookmarkDao.upsert(any()) } returns Unit

        // When
        repository.saveBookmark(article)

        // Then
        coVerify { mockBookmarkDao.upsert(any()) }
    }

    @Test
    fun `removeBookmark deletes by id`() = runBlocking {
        // Given
        coEvery { mockBookmarkDao.deleteById(any()) } returns Unit

        // When
        repository.removeBookmark("1")

        // Then
        coVerify { mockBookmarkDao.deleteById("1") }
    }

    @Test
    fun `isBookmarked returns correct value`() = runBlocking {
        // Given
        coEvery { mockBookmarkDao.isBookmarked("1") } returns true

        // When
        val result = repository.isBookmaarked("1")

        // Then
        assertTrue(result)
    }

    @Test
    fun `insertSearchQuery inserts search history`() = runBlocking {
        // Given
        coEvery { mockNewsSyncAndSearchDao.insertSearchHistory(any()) } returns Unit

        // When
        repository.insertSearchQuery("test query")

        // Then
        coVerify { mockNewsSyncAndSearchDao.insertSearchHistory(
            // Verify that the correct query was inserted.
            // We check only the 'query' field because 'id' is auto-generated and 'ts' (timestamp) changes dynamically.

//            SearchHistoryEntity(query = "test query")   // checks all..  --> causes error manly by dunamic ts (time in milisec)
//            SearchHistoryEntity(id = 0, query = "test query", ts = any() )  // not checks ts --> so can be use this also
            match { it.query == "test query" } // check only query, ignores ts/id  --> this is more preferable

            )
        }
    }

    @Test
    fun `insertNews inserts news entities`() = runBlocking {
        // Given
        val newsList = listOf(
            NewsEntity(
                id = 1,
                title = "Test Title",
                content = "Test Content",
                url = "http://test.com/article"
            )
        )

        coEvery { mockNewsSyncAndSearchDao.insertNews(any()) } returns Unit

        // When
        repository.insertNews(newsList)

        // Then
        coVerify { mockNewsSyncAndSearchDao.insertNews(newsList) }
    }

    @Test
    fun `ftsSearch returns search results`() = runBlocking {
        // Given
        val mockFtsEntities = listOf(
            NewsFtsEntity(
                title = "Test Title",
                content = "Test content that is long enough to be truncated",
                url = "http://test.com/article"
            )
        )

        val expectedFtsRows = listOf(
            FtsRow("Test Title", "Test content that is long enough to be truncated".take(200), "http://test.com/article")
        )

        coEvery { mockNewsSyncAndSearchDao.searchFts("test", 50) } returns mockFtsEntities

        // When
        val result = repository.ftsSearch("test")

        // Then
        assertEquals(expectedFtsRows, result)
    }

    @Test
    fun `observeSearchHistory returns flow`() = runBlocking {
        // Given
        val mockHistory = listOf(SearchHistoryEntity(query = "test query"))
        every { mockNewsSyncAndSearchDao.getSearchHistoryFlow() } returns flowOf(mockHistory)

        // When
        val result = repository.observeSearchHistory().first()

        // Then
        assertEquals(mockHistory, result)
    }

    @Test
    fun `clearSearchHistory clears history`() = runBlocking {
        // Given
        coEvery { mockNewsSyncAndSearchDao.clearSearchHistory() } returns Unit

        // When
        repository.clearSearchHistory()

        // Then
        coVerify { mockNewsSyncAndSearchDao.clearSearchHistory() }
    }

    @Test
    fun `clearAll clears news`() = runBlocking {
        // Given
        coEvery { mockNewsSyncAndSearchDao.clearNews() } returns Unit

        // When
        repository.clearAll()

        // Then
        coVerify { mockNewsSyncAndSearchDao.clearNews() }
    }

    @Test
    fun `getBookmarksToImport upserts bookmarks`() = runBlocking {
        // Given
        val bookmarks = listOf(
            BookmarkEntity(
                id = "1",
                title = "Test Title 1",
                url = "http://test.com/article1",
                sourceName = "Test Source",
                imageUrl = "http://test.com/image1.jpg"
            )
        )

        coEvery { mockBookmarkDao.upsertAllForImport(any()) } returns Unit

        // When
        repository.getBookmarksToImport(bookmarks)

        // Then
        coVerify { mockBookmarkDao.upsertAllForImport(bookmarks) }
    }

    @Test
    fun `getBookmarksToExport returns bookmarks`() = runBlocking {
        // Given
        val expectedBookmarks = listOf(
            BookmarkEntity(
                id = "1",
                title = "Test Title 1",
                url = "http://test.com/article1",
                sourceName = "Test Source",
                imageUrl = "http://test.com/image1.jpg"
            )
        )

        coEvery { mockBookmarkDao.getAllForExport() } returns expectedBookmarks

        // When
        val result = repository.getBookmarksToExport()

        // Then
        assertEquals(expectedBookmarks, result)
    }
}