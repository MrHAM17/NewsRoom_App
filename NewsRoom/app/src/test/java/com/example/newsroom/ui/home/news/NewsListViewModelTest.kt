package com.example.newsroom.ui.home.news

import com.example.newsroom.data.remote.model.dto.Article
import com.example.newsroom.data.repository.NewsRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class NewsListViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val testArticle = Article(
        id = "https://example.com/a1",
        sourceId = null,
        sourceName = "Source",
        author = "Author",
        title = "T1",
        description = "D",
        url = "https://example.com/a1",
        imageUrl = null,
        publishedAtUtc = null,
        content = null
    )

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadTopHeadlines updates state with articles when repo emits success`() = runTest {
        val repo = mockk<NewsRepository>()

        // ✅ Explicit type argument for safeNetworkCall
        coEvery {
            repo.safeNetworkCall<Unit>(any(), any<suspend () -> Unit>())
        } returns Result.success(Unit)

        // topHeadlines returns a Flow<Result<List<Article>>> with one article
        coEvery {
            repo.topHeadlines("general")
        } returns flowOf(Result.success(listOf(testArticle)))

        val vm = NewsListViewModel(repo)
        vm.loadTopHeadlines("general", mockk(relaxed = true))

        // let launched coroutines run
        advanceUntilIdle()

        // ✅ collect the flow instead of casting
        val state = vm.state.first()

        assertEquals(1, state.articles.size)
        assertEquals("T1", state.articles.first().title)
        assertEquals(null, state.error)
    }

    @Test
    fun `loadTopHeadlines sets error message when network check fails`() = runTest {
        val repo = mockk<NewsRepository>()

        // ✅ Explicit type argument for failure case
        coEvery {
            repo.safeNetworkCall<Unit>(any(), any<suspend () -> Unit>())
        } returns Result.failure(Exception("No network"))

        // topHeadlines will still be called but return a failure flow
        coEvery {
            repo.topHeadlines("general")
        } returns flowOf(Result.failure(Exception("API fail")))

        val vm = NewsListViewModel(repo)
        vm.loadTopHeadlines("general", mockk(relaxed = true))

        advanceUntilIdle()

        val state = vm.state.first()

        // Because safeNetworkCall failed, error message is attached
        assertEquals("No network", state.error)
    }
}
