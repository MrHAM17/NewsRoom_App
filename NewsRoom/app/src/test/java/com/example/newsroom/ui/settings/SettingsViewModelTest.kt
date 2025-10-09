package com.example.newsroom.ui.settings

import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.example.newsroom.data.repository.NewsRepository
import com.example.newsroom.ui.settings.importExport.BookmarkImport
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import com.example.newsroom.data.local.room.entity.BookmarkEntity
import com.example.newsroom.ui.settings.importExport.BookmarkExport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import kotlinx.coroutines.channels.Channel


@ExperimentalCoroutinesApi
class MainCoroutineRule(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class SettingsViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var mockRepository: NewsRepository
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setup() {
        mockRepository = mockk(relaxed = true)
        viewModel = SettingsViewModel(
            ApplicationProvider.getApplicationContext(),
            mockRepository
        )
    }

    @Test
    fun exportToShouldCallRepositoryAndReturnSuccess() = runTest {
        // Given
        val mockUri = mockk<Uri>()
        val format = BookmarkExport.Format.CSV
        val mockBookmarks = listOf(mockk<BookmarkEntity>(relaxed = true))
        coEvery { mockRepository.getBookmarksToExport() } returns mockBookmarks
        mockkObject(BookmarkExport)
        every { BookmarkExport.write(any(), any(), any(), any()) } just Runs

//        // When
//        var result: Boolean? = null
//        viewModel.exportTo(mockUri, format) { result = it }
//        // Wait for coroutine to complete
//        mainCoroutineRule.testDispatcher.scheduler.advanceUntilIdle()

        // When - Use Channel pattern for consistency
        val resultChannel = Channel<Boolean>(1)
        viewModel.exportTo(mockUri, format) { result ->
            resultChannel.trySend(result).isSuccess
        }
        // Wait for the result
        val result = resultChannel.receive()


        // Then
        coVerify { mockRepository.getBookmarksToExport() }
        Assert.assertTrue(result == true)
        unmockkObject(BookmarkExport)
    }

    @Test
    fun importFromShouldCallRepositoryAndReturnCount() = runTest {
        // Given
        val mockUri = mockk<Uri>()
        val mockBookmarks = listOf(
            mockk<BookmarkEntity> {
                every { id } returns "test-id"
                every { url } returns "test-url"
                every { title } returns "test-title"
                every { sourceName } returns "test-source"
                every { imageUrl } returns "test-image"
                every { saveAt } returns 1234567890L
            }
        )

        mockkObject(BookmarkImport)
        every { BookmarkImport.read(any(), any()) } returns mockBookmarks
        coJustRun { mockRepository.getBookmarksToImport(any()) }

//        // When
//        var result: Int? = null
//        viewModel.importFrom(mockUri) { result = it }
//        // Wait for coroutine to complete
//        mainCoroutineRule.testDispatcher.scheduler.advanceUntilIdle()


        // When
        val resultChannel = Channel<Int>(1)
        viewModel.importFrom(mockUri) { result ->
            resultChannel.trySend(result).isSuccess
        }
        // Wait for the result
        val result = resultChannel.receive()



        // Then
        coVerify { mockRepository.getBookmarksToImport(mockBookmarks) }
        Assert.assertEquals(mockBookmarks.size, result)
        unmockkObject(BookmarkImport)
    }

    @Test
    fun clearAllBookmarksShouldCallRepositoryAndReturnSuccess() = runTest {
        // Given
        coEvery { mockRepository.removeAllBookmarks() } returns Unit

//        // When
//        var result: Boolean? = null
//        viewModel.clearAllBookmarks { result = it }
//        // Wait for coroutine to complete
//        mainCoroutineRule.testDispatcher.scheduler.advanceUntilIdle()

        // When
        val resultChannel = Channel<Boolean>(1)
        viewModel.clearAllBookmarks { result ->
            resultChannel.trySend(result).isSuccess
        }
        // Wait for the result
        val result = resultChannel.receive()



        // Then
        coVerify { mockRepository.removeAllBookmarks() }
        Assert.assertTrue(result == true)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }
}