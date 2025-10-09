package com.example.newsroom.ui.sources

import app.cash.turbine.test
import com.example.newsroom.data.remote.model.SourcesResponse
import com.example.newsroom.data.remote.model.dto.SourceDto
import com.example.newsroom.data.repository.NewsRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SourcesViewModelTest {

    private lateinit var repo: NewsRepository
    private lateinit var viewModel: SourcesViewModel

    // Test dispatcher for Main
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        // Set Main dispatcher to test dispatcher
        Dispatchers.setMain(testDispatcher)

        repo = mockk(relaxed = true)
        viewModel = SourcesViewModel(repo)
    }

    @After
    fun tearDown() {
        // Reset Main dispatcher after test
        Dispatchers.resetMain()
    }

    @Test
    fun `load sets state correctly on success`() = runTest {
        val sourcesList = listOf(
            SourceDto("abc-news", "ABC News", "News from ABC", "https://abcnews.com", "general", "en", "us")
        )
        val response = SourcesResponse(status = "ok", sources = sourcesList)

        coEvery { repo.sources(null) } returns response

        viewModel.state.test {
            viewModel.load()
            testScheduler.advanceUntilIdle()

            var finalState: SourcesState? = null
            // Consume all emissions until completion
            while (true) {
                val item = awaitItem()
                finalState = item
                if (!item.loading) break
            }

            finalState!!.let { state ->
                assertFalse(state.loading)
                assertEquals(sourcesList, state.list)
                assertNull(state.error)
            }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `load sets state correctly on failure`() = runTest {
        val exception = RuntimeException("Network Error")
        coEvery { repo.sources(null) } throws exception

        viewModel.state.test {
            viewModel.load()
            testScheduler.advanceUntilIdle()

            var finalState: SourcesState? = null
            while (true) {
                val item = awaitItem()
                finalState = item
                if (!item.loading) break
            }

            finalState!!.let { state ->
                assertFalse(state.loading)
                assertTrue(state.list.isEmpty())
                assertEquals("Network Error", state.error)
            }

            cancelAndIgnoreRemainingEvents()
        }
    }

}
