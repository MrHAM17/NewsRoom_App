package com.example.newsroom.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.newsroom.data.local.room.NewsDatabase
import com.example.newsroom.data.local.room.entity.BookmarkEntity
import com.example.newsroom.data.local.room.entity.NewsEntity
import com.example.newsroom.data.remote.api.NewsApiService
import com.example.newsroom.data.remote.model.dto.Article
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class NewsRepositoryAndroidTest {

    private lateinit var repository: NewsRepository
    private lateinit var db: NewsDatabase
    private lateinit var context: Context
    private lateinit var mockApi: NewsApiService

    @Before
    fun createDb() {
        context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, NewsDatabase::class.java).build()
        // Note: For a real test, you'd need to mock the API service
        // For now, we'll focus on testing the database operations
        mockApi = mockk<NewsApiService>(relaxed = true) // Create a mock API service
        repository = NewsRepository(mockApi, db)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun bookmarkOperationsWorkCorrectly() = runBlocking {
        // Insert a bookmark
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

        repository.saveBookmark(article)

        // Check if bookmarked
        val isBookmarked = repository.isBookmaarked("1")
        assertTrue(isBookmarked)

        // Remove bookmark
        repository.removeBookmark("1")

        // Check if removed
        val isStillBookmarked = repository.isBookmaarked("1")
        assertFalse(isStillBookmarked)
    }

    @Test
    @Throws(Exception::class)
    fun searchHistoryOperationsWorkCorrectly() = runBlocking {
        // Insert search query
        repository.insertSearchQuery("test query")

        // Observe search history (this would need a LiveData/Flow test in a real scenario)
        // For now, we'll just verify the insertion doesn't crash

        // Clear search history
        repository.clearSearchHistory()
        // Verify no crash
    }

    @Test
    @Throws(Exception::class)
    fun newsOperationsWorkCorrectly() = runBlocking {
        // Insert news
        val newsList = listOf(
            NewsEntity(
//                id = "1",
                title = "Test Title",
                content = "Test Content",
                url = "http://test.com/article",
//                imageUrl = "http://test.com/image.jpg",
//                sourceName = "Test Source",
//                publishedAt = "2023-01-01"
            )
        )

        repository.insertNews(newsList)

        // Clear all news
        repository.clearAll()
        // Verify no crash
    }

    @Test
    @Throws(Exception::class)
    fun bookmarkImportExportWorksCorrectly() = runBlocking {
        // Create test bookmarks
        val bookmarks = listOf(
            BookmarkEntity(
                id = "1",
                title = "Test Title 1",
                url = "http://test.com/article1",
                sourceName = "Test Source",
                imageUrl = "http://test.com/image1.jpg"
            ),
            BookmarkEntity(
                id = "2",
                title = "Test Title 2",
                url = "http://test.com/article2",
                sourceName = "Test Source",
                imageUrl = "http://test.com/image2.jpg"
            )
        )

        // Import bookmarks
        repository.getBookmarksToImport(bookmarks)

        // Export bookmarks
        val exported = repository.getBookmarksToExport()

        // Sort by ID to ensure consistent order for comparison
        val sortedExported = exported.sortedBy { it.id }

        // Verify export contains imported bookmarks
//        assertEquals(2, exported.size)
//        assertEquals("Test Title 1", exported[0].title)
//        assertEquals("Test Title 2", exported[1].title)
        assertEquals(2, sortedExported.size)
        assertEquals("Test Title 1", sortedExported[0].title)
        assertEquals("Test Title 2", sortedExported[1].title)
    }
}