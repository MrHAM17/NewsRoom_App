package com.example.newsroom.core.workers

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.testing.TestListenableWorkerBuilder
import com.example.newsroom.data.remote.model.dto.Article
import com.example.newsroom.data.repository.NewsRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NewsSyncWorkerTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun workerReturnsSuccessWhenRepoSyncSucceeds() = runBlocking {
        val repo = mockk<NewsRepository>()

        // Mock the 'everything' call that NewsSyncWorker actually uses
        val testArticle = Article(
            id = "1",
            sourceId = null,
            sourceName = "Test",
            author = "Author",
            title = "Title",
            description = "Desc",
            url = "https://example.com",
            imageUrl = null,
            publishedAtUtc = null,
            content = null
        )
        coEvery { repo.everything("india", 1) } returns flowOf(Result.success(listOf(testArticle)))
        coEvery { repo.insertNews(any()) } returns Unit

        // Build worker
        val worker = TestListenableWorkerBuilder<NewsSyncWorker>(context)
            .setWorkerFactory(object : androidx.work.WorkerFactory() {
                override fun createWorker(
                    appContext: Context,
                    workerClassName: String,
                    workerParameters: androidx.work.WorkerParameters
                ) = NewsSyncWorker(appContext, workerParameters, repo)
            })
            .build()

        // Execute work
        val result = worker.doWork()

        assertEquals(androidx.work.ListenableWorker.Result.success()::class, result::class)
    }
}
