package com.example.newsroom.ui.common.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.newsroom.data.repository.NewsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.test.assertEquals

class DetailViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // small LiveData test helper with CountDownLatch
    private fun <T> LiveData<T>.getOrAwaitValue(
        time: Long = 2,
        unit: TimeUnit = TimeUnit.SECONDS
    ): T {
        var data: T? = null
        val latch = CountDownLatch(1)
        val observer = object : Observer<T> {
            override fun onChanged(value: T) {
                data = value
                latch.countDown()
                this@getOrAwaitValue.removeObserver(this)
            }
        }
        this.observeForever(observer)
        if (!latch.await(time, unit)) {
            this.removeObserver(observer)
            throw TimeoutException("LiveData value was never set.")
        }
        @Suppress("UNCHECKED_CAST")
        return data as T
    }

    @Test
    fun `checkIsBookmarked sets isBookmarked true when repo returns true`() = runBlocking {
        val repo = mockk<NewsRepository>()
        coEvery { repo.isBookmaarked("id-1") } returns true

        val vm = DetailViewModel(repo)
        vm.checkIsBookmarked("id-1")

        // wait until LiveData updates
        val result = vm.isBookmarked.getOrAwaitValue()
        assertEquals(true, result)
    }

    @Test
    fun `saveBookmark calls repo and sets isBookmarked true`() = runBlocking {
        val repo = mockk<NewsRepository>(relaxed = true)
        coEvery { repo.saveBookmark(any()) } returns Unit

        val vm = DetailViewModel(repo)
        vm.saveBookmark(
            id = "id-1",
            title = "T",
            url = "https://x",
            source = "S",
            image = "img"
        )

        val result = vm.isBookmarked.getOrAwaitValue()
        assertEquals(true, result)

        // verify repo called with an Article that contains same id/title/url
        coVerify { repo.saveBookmark(match { it.id == "id-1" && it.title == "T" && it.url == "https://x" }) }
    }

    @Test
    fun `removeBookmark calls repo and emits bookmarkRemoved true`() = runBlocking {
        val repo = mockk<NewsRepository>(relaxed = true)
        coEvery { repo.removeBookmark("id-1") } returns Unit

        val vm = DetailViewModel(repo)
        vm.removeBookmark("id-1")

        val isBookmarkedValue = vm.isBookmarked.getOrAwaitValue()
        val bookmarkRemovedValue = vm.bookmarkRemoved.getOrAwaitValue()

        assertEquals(false, isBookmarkedValue)
        assertEquals(true, bookmarkRemovedValue)

        coVerify { repo.removeBookmark("id-1") }
    }
}
