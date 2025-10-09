package com.example.newsroom.ui.saved

import app.cash.turbine.test
import com.example.newsroom.data.local.room.entity.BookmarkEntity
import com.example.newsroom.data.repository.NewsRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class SavedViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `bookmarks emits empty list when repo is empty`() = runTest {
        val repo = mockk<NewsRepository>()
        every { repo.observeBookmarks() } returns flowOf(emptyList())

        val vm = SavedViewModel(repo)

        vm.bookmarks.test {
            val items = awaitItem()
            assertEquals(0, items.size)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `bookmarks emits list when repo has items`() = runTest {
        val repo = mockk<NewsRepository>()
        val fake = listOf(
            BookmarkEntity(
                id = "1",
                title = "Sample",
                url = "https://example.com",
                sourceName = "Source",
                imageUrl = null
            )
        )
        every { repo.observeBookmarks() } returns flowOf(fake)

        val vm = SavedViewModel(repo)

        vm.bookmarks.test {
            assertEquals(0, awaitItem().size)   // first emission is always emptyList()

            val items = awaitItem()  // second emission comes from repo
            assertEquals(1, items.size)
            assertEquals("Sample", items.first().title)
            assertEquals("Source", items.first().sourceName)
            cancelAndConsumeRemainingEvents()
        }
    }
}
