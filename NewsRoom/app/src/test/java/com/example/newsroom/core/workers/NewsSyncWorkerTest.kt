package com.example.newsroom.core.workers

import android.content.Context
import android.util.Log
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.example.newsroom.core.notifications.NotificationHelper
import com.example.newsroom.data.remote.model.dto.Article
import com.example.newsroom.data.repository.NewsRepository
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.sql.SQLException
import kotlin.test.assertEquals
import kotlin.Result as KResult // avoid conflict with Worker.Result

class NewsSyncWorkerTest {

    private lateinit var repo: NewsRepository
    private lateinit var context: Context
    private lateinit var workerParams: WorkerParameters

    @Before
    fun setup() {
        // Mock logs and notifications
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any<Throwable>()) } returns 0

        repo = mockk()
        context = mockk(relaxed = true)
        workerParams = mockk()
        mockkObject(NotificationHelper)
        every { NotificationHelper.notifySync(context, any()) } just Runs
    }

    private fun fakeArticle(id: String = "1") = Article(
        id = id, sourceId = "s", sourceName = "S", author = "A",
        title = "Title$id", description = "Desc$id", url = "url$id",
        imageUrl = null, publishedAtUtc = null, content = null
    )

    // -----------------------------
    // 1️⃣ Both endpoints succeed → success
    // -----------------------------
    @Test
    fun `success when both endpoints succeed`() = runTest {
        coEvery { repo.topHeadlines(any()) } returns flowOf(KResult.success(listOf(fakeArticle("T"))))
        coEvery { repo.everything("india", page = 1) } returns flowOf(KResult.success(listOf(fakeArticle("E"))))
        coEvery { repo.insertNews(any()) } just Runs

        val worker = NewsSyncWorker(context, workerParams, repo)
        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.success(), result)
        coVerify { repo.insertNews(match { it.size == 8 }) } // ✅ 7 top + 1 everything
        verify { NotificationHelper.notifySync(context, "News Synced") }
    }

    // -----------------------------
    // 2️⃣ Top headlines fail temporarily, everything succeed → success
    // -----------------------------
    @Test
    fun `success when top headlines fail temporarily but everything succeeds`() = runTest {
        coEvery { repo.topHeadlines(any()) } returns flowOf(KResult.failure(IOException("Network fail")))
        coEvery { repo.everything("india", page = 1) } returns flowOf(KResult.success(listOf(fakeArticle("E"))))
        coEvery { repo.insertNews(any()) } just Runs

        val worker = NewsSyncWorker(context, workerParams, repo)
        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.success(), result)
        coVerify { repo.insertNews(match { it.size == 1 }) }
    }

    // -----------------------------
    // 3️⃣ Top headlines succeed, everything fail temporarily → success
    // -----------------------------
    @Test
    fun `success when top headlines succeed but everything fails temporarily`() = runTest {
        coEvery { repo.topHeadlines(any()) } returns flowOf(KResult.success(listOf(fakeArticle("T"))))
        coEvery { repo.everything("india", page = 1) } returns flowOf(KResult.failure(IOException("Network down")))
        coEvery { repo.insertNews(any()) } just Runs

        val worker = NewsSyncWorker(context, workerParams, repo)
        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.success(), result)
        coVerify { repo.insertNews(match { it.size == 7 }) } // ✅ Only 7 top headlines
    }

    // -----------------------------
    // 4️⃣ Both endpoints fail temporarily → retry
    // -----------------------------
    @Test
    fun `retry when both endpoints fail temporarily`() = runTest {
        coEvery { repo.topHeadlines(any()) } returns flowOf(KResult.failure(IOException("Network fail")))
        coEvery { repo.everything("india", page = 1) } returns flowOf(KResult.failure(IOException("Network fail")))

        val worker = NewsSyncWorker(context, workerParams, repo)
        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.retry(), result)
    }

    // -----------------------------
    // 5️⃣ Top headlines succeed, everything fail permanently → failure
    // -----------------------------
    @Test
    fun `success when top headlines succeed but everything fails permanently`() = runTest {
        coEvery { repo.topHeadlines(any()) } returns flowOf(KResult.success(listOf(fakeArticle("T"))))
        coEvery { repo.everything("india", page = 1) } returns flowOf(KResult.failure(SQLException("DB error")))
        coEvery { repo.insertNews(any()) } just Runs

        val worker = NewsSyncWorker(context, workerParams, repo)
        val result = worker.doWork()

        // Should still save top headlines → success
        assertEquals(ListenableWorker.Result.success(), result)
        coVerify { repo.insertNews(match { it.size == 7 }) } // ✅ Only 7 top headlines
    }

    // -----------------------------
    // 6️⃣ Top headlines fail permanently, everything succeed → success
    // -----------------------------
    @Test
    fun `success when top headlines fail permanently but everything succeeds`() = runTest {
        coEvery { repo.topHeadlines(any()) } returns flowOf(KResult.failure(SQLException("DB error")))
        coEvery { repo.everything("india", page = 1) } returns flowOf(KResult.success(listOf(fakeArticle("E"))))
        coEvery { repo.insertNews(any()) } just Runs

        val worker = NewsSyncWorker(context, workerParams, repo)
        val result = worker.doWork()

        // Should save everything → success
        assertEquals(ListenableWorker.Result.success(), result)
        coVerify { repo.insertNews(match { it.size == 1 }) }
    }

    // -----------------------------
    // 7️⃣ Top headlines fail permanently, everything fail permanently → failure
    // -----------------------------
    @Test
    fun `failure when both endpoints fail permanently`() = runTest {
        coEvery { repo.topHeadlines(any()) } returns flowOf(KResult.failure(SQLException("DB error")))
        coEvery { repo.everything("india", page = 1) } returns flowOf(KResult.failure(SQLException("DB error")))

        val worker = NewsSyncWorker(context, workerParams, repo)
        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.failure(), result)
    }

    // -----------------------------
    // 8️⃣ Top headlines fail temporarily, everything fail permanently → retry
    // -----------------------------
    @Test
    fun `retry when top headlines fail temporarily and everything fails permanently`() = runTest {
        coEvery { repo.topHeadlines(any()) } returns flowOf(KResult.failure(IOException("Network fail")))
        coEvery { repo.everything("india", page = 1) } returns flowOf(KResult.failure(SQLException("DB error")))

        val worker = NewsSyncWorker(context, workerParams, repo)
        val result = worker.doWork()

        // Temporary network failure triggers retry even though everything fails permanently
        assertEquals(ListenableWorker.Result.retry(), result)
    }

    // -----------------------------
    // 9️⃣ Both endpoints throw unexpected exceptions → failure
    // -----------------------------
    @Test
    fun `failure when both endpoints throw unexpected exceptions`() = runTest {
        coEvery { repo.topHeadlines(any()) } returns flowOf(KResult.failure(IllegalStateException("Unexpected")))
        coEvery { repo.everything("india", page = 1) } returns flowOf(KResult.failure(IllegalArgumentException("Unexpected")))

        val worker = NewsSyncWorker(context, workerParams, repo)
        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.failure(), result)
    }

    // -----------------------------
    // 🔄 Mixed: Top partially succeeds, others fail temporarily → retry
    // -----------------------------
    @Test
    fun `retry when top partially succeeds and everything fails temporarily`() = runTest {
        coEvery { repo.topHeadlines("general") } returns flowOf(KResult.success(listOf(fakeArticle("T1"))))
        coEvery { repo.topHeadlines("business") } returns flowOf(KResult.failure(IOException("Network fail")))
        coEvery { repo.everything("india", page = 1) } returns flowOf(KResult.failure(IOException("Network fail")))

        val worker = NewsSyncWorker(context, workerParams, repo)
        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.retry(), result)
        coVerify { repo.insertNews(match { it.size == 1 }) } // Only successfully fetched top
    }

}
