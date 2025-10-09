// NewsWidgetUnitTest.kt
package com.example.newsroom.widget

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.newsroom.core.di.WidgetEntryPoint
import com.example.newsroom.data.remote.model.dto.Article
import com.example.newsroom.data.repository.NewsRepository
import dagger.hilt.android.EntryPointAccessors
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class NewsRemoteViewsFactoryTest {

    private lateinit var context: Context
    private lateinit var factory: NewsRemoteViewsFactory
    private lateinit var mockEntryPoint: WidgetEntryPoint
    private lateinit var mockRepository: NewsRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        mockRepository = mockk()
        mockEntryPoint = mockk()

        // Mock Hilt entry point
        every { mockEntryPoint.newsRepository() } returns mockRepository
        mockkStatic(EntryPointAccessors::class)
        every { EntryPointAccessors.fromApplication(any(), WidgetEntryPoint::class.java) } returns mockEntryPoint

        factory = NewsRemoteViewsFactory(context)
    }

    @Test
    fun `onDataSetChanged should fetch articles from repository`() = runBlocking {
        // Given
        val mockArticles = listOf(
            Article(
                id = "1",
                sourceId = null, // Added missing parameter
                sourceName = "Test Source",
                author = "Test Author",
                title = "Test Title 1",
                description = "Test Description",
                url = "http://test.com/article1", // Changed from imageUrl to url
                imageUrl = "http://test.com/image1.jpg",
                publishedAtUtc = "2023-01-01",
                content = "Test Content"
            ),
            Article(
                id = "2",
                sourceId = null, // Added missing parameter
                sourceName = "Test Source",
                author = "Test Author",
                title = "Test Title 2",
                description = "Test Description",
                url = "http://test.com/article2", // Changed from imageUrl to url
                imageUrl = "http://test.com/image2.jpg",
                publishedAtUtc = "2023-01-01",
                content = "Test Content"
            )
        )
        coEvery { mockRepository.topHeadlines("general") } returns flowOf(Result.success(mockArticles))

        // When
        factory.onDataSetChanged()

        // Then
        assertEquals(2, factory.getCount())
    }

    @Test
    fun `onDataSetChanged should handle empty articles`() = runBlocking {
        // Given
        coEvery { mockRepository.topHeadlines("general") } returns flowOf(Result.success(emptyList()))

        // When
        factory.onDataSetChanged()

        // Then
        assertEquals(0, factory.getCount())
    }

    @Test
    fun `onDataSetChanged should handle repository error`() = runBlocking {
        // Given
        coEvery { mockRepository.topHeadlines("general") } returns flowOf(Result.failure(Exception("API Error")))

        // When
        factory.onDataSetChanged()

        // Then
        assertEquals(0, factory.getCount())
    }

    @Test
    fun `getViewAt should return RemoteViews with correct data`() {
        // Given
        val article = Article(
            id = "1",
            sourceId = null, // Added missing parameter
            title = "Test Title",
            imageUrl = "http://test.com/image.jpg",
            url = "http://test.com",
            sourceName = "Test Source",
            author = "Test Author",
            publishedAtUtc = "2023-01-01",
            description = "Test Description",
            content = "Test Content"
        )

        // Use reflection to set the articles list
        val articlesField = factory::class.java.getDeclaredField("articles")
        articlesField.isAccessible = true
        articlesField.set(factory, listOf(article))

        // When
        val remoteViews = factory.getViewAt(0)

        // Then
        assertNotNull(remoteViews)
    }

    @Test
    fun `getViewAt should handle null image URL`() {
        // Given
        val article = Article(
            id = "1",
            sourceId = null, // Added missing parameter
            title = "Test Title",
            imageUrl = null,
            url = "http://test.com",
            sourceName = "Test Source",
            author = "Test Author",
            publishedAtUtc = "2023-01-01",
            description = "Test Description",
            content = "Test Content"
        )

        // Use reflection to set the articles list
        val articlesField = factory::class.java.getDeclaredField("articles")
        articlesField.isAccessible = true
        articlesField.set(factory, listOf(article))

        // When
        val remoteViews = factory.getViewAt(0)

        // Then
        assertNotNull(remoteViews)
    }

    @Test
    fun `loadBitmapFromUrl should return null on exception`() {
        // Given
        val invalidUrl = "invalid-url"

        // When
        val result = factory.loadBitmapFromUrl(invalidUrl)

        // Then
        assertNull(result)
    }

    @Test
    fun `getItemId should return correct id`() {
        // Given
        val position = 3

        // When
        val result = factory.getItemId(position)

        // Then
        assertEquals(3L, result)
    }

    @Test
    fun `hasStableIds should return true`() {
        // When
        val result = factory.hasStableIds()

        // Then
        assertEquals(true, result)
    }

    @Test
    fun `getViewTypeCount should return 1`() {
        // When
        val result = factory.getViewTypeCount()

        // Then
        assertEquals(1, result)
    }

    @Test
    fun `getLoadingView should return null`() {
        // When
        val result = factory.getLoadingView()

        // Then
        assertNull(result)
    }
}