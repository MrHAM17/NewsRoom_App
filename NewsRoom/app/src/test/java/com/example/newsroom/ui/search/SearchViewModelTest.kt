package com.example.newsroom.ui.search

import app.cash.turbine.test
import com.example.newsroom.data.remote.model.dto.Article
import com.example.newsroom.data.repository.NewsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class SearchViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun sampleArticle(idSuffix: String) = Article(
        id = "id-$idSuffix",
        sourceId = "s$idSuffix",
        sourceName = "Source$idSuffix",
        author = "Author",
        title = "Title $idSuffix",
        description = "Desc",
        url = "https://example.com/$idSuffix",
        imageUrl = null,
        publishedAtUtc = null,
        content = null
    )

    @Test
    fun `searchEverything emits articles when repo everything returns success`() = runTest {
        val repo = mockk<NewsRepository>()
        val article = sampleArticle("1")

        // stubs
        coEvery { repo.everything("india") } returns flowOf(Result.success(listOf(article)))
        coEvery { repo.insertSearchQuery(any()) } returns Unit

        val vm = SearchViewModel(repo)
        val flow = vm.searchEverything("india")

        flow.test {
            val loadingState = awaitItem()
            assertEquals(true, loadingState.loading) // first emission

            val finalState = awaitItem()
            assertEquals(false, finalState.loading)
            assertEquals(1, finalState.articles.size)
            assertEquals("Title 1", finalState.articles[0].title)
            assertEquals(null, finalState.error)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `searchEverything falls back to fts when repo everything fails`() = runTest {
        val repo = mockk<NewsRepository>()

        coEvery { repo.everything("q") } returns flowOf(Result.failure(Exception("api failed")))
        coEvery { repo.ftsSearch("q") } returns listOf(
            com.example.newsroom.data.local.room.entity.FtsRow("FTS title", "snippet", "https://u")
        )

        val vm = SearchViewModel(repo)
        val flow = vm.searchEverything("q")

        flow.test {
            val loadingState = awaitItem()
            assertEquals(true, loadingState.loading) // first emission

            val finalState = awaitItem()
            assertEquals(false, finalState.loading)
            assertEquals(1, finalState.articles.size)
            assertEquals("FTS title", finalState.articles[0].title)
            assertEquals("api failed", finalState.error)

            cancelAndConsumeRemainingEvents()
        }
        // ensure query is NOT inserted when API fails
        coVerify(exactly = 0) { repo.insertSearchQuery(any()) }
    }
}
