
package com.example.newsroom.core.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.newsroom.data.local.room.entity.NewsEntity
import com.example.newsroom.data.repository.NewsRepository
import com.example.newsroom.core.notifications.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.sql.SQLException

@HiltWorker
class NewsSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: NewsRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("SyncWorker", "Sync task started")

        val newsList = mutableListOf<NewsEntity>()
        var shouldRetry = false


//        try {
//
//            withContext(Dispatchers.IO) {
//
//                val newsList = mutableListOf<NewsEntity>()
//
////                // 1️⃣ Fetch Top Headlines category-wise
////                val categories = listOf( "general", "business", "entertainment", "health", "science", "sports", "technology" )
////                for (cat in categories) {
////                    repository.topHeadlines(category = cat).collect { res ->
////                        res.onSuccess { articles ->
////                            newsList.addAll(
////                                articles.map { article ->
////                                    NewsEntity(
////                                        title = article.title,
////                                        content = article.description ?: article.content ?: "",
////                                        url = article.url
////                                    )
////                                }
////                            )
////                        }
////                        res.onFailure { e ->
////                            Log.e("SyncWorker", "TopHeadlines failed for $cat", e)
////                        }
////                    }
////                }
//
//                //  2️⃣ Fetch "everything" query (e.g., "india")
//                val result = repository.everything("india", page = 1)
//                result.collect { res ->
//                    res.onSuccess { list ->
//                        val newsList = list.map { article ->
//                            NewsEntity(
//                                title = article.title,
//                                content = article.description ?: article.content ?: "",
//                                url = article.url
//                            )
//                        }
//                        repository.insertNews(newsList)
//                    }
//                    res.onFailure { e ->
//                        throw e  // Worker will retry/fail accordingly
//                    }
//                }
//
//
////                // 3️⃣  Insert only into NewsEntity
////                if (newsList.isNotEmpty()) {
////                    repository.insertNews(newsList)
////                    // Room will automatically update FTS table
////                }
//
//            }
//
//            NotificationHelper.notifySync(applicationContext, "News Synced")
//            Log.d("SyncWorker", "Sync task completed")
//            Result.success()
//        }


        try {
            withContext(Dispatchers.IO) {
                val categories = listOf(
                    "general", "business", "entertainment",
                    "health", "science", "sports", "technology"
                )

                // -----------------------------
                // 1️⃣ Top headlines
                // -----------------------------
                for (cat in categories) {
                    try {
                        repository.topHeadlines(cat).collect { res ->
                            res.onSuccess { articles ->
                                newsList.addAll(
                                    articles.map { article ->
                                        NewsEntity(
                                            title = article.title,
                                            content = article.description ?: article.content ?: "",
                                            url = article.url
                                        )
                                    }
                                )
                            }
                            res.onFailure { e ->
                                Log.e("SyncWorker", "TopHeadlines failed for $cat", e)
                                if (e is IOException) shouldRetry = true
                            }
                        }
                    } catch (e: IOException) {
                        Log.e("SyncWorker", "TopHeadlines IOException for $cat", e)
                        shouldRetry = true
                    } catch (e: SQLException) {
                        Log.e("SyncWorker", "TopHeadlines DB error for $cat", e)
                        throw e // fail immediately
                    } catch (e: Exception) {
                        Log.e("SyncWorker", "TopHeadlines unexpected error for $cat", e)
                    }
                }

                // -----------------------------
                // 2️⃣ Everything
                // -----------------------------
                try {
                    repository.everything("india", page = 1).collect { res ->
                        res.onSuccess { articles ->
                            newsList.addAll(
                                articles.map { article ->
                                    NewsEntity(
                                        title = article.title,
                                        content = article.description ?: article.content ?: "",
                                        url = article.url
                                    )
                                }
                            )
                        }
                        res.onFailure { e ->
                            Log.e("SyncWorker", "Everything endpoint failed", e)
                            if (e is IOException) shouldRetry = true
                        }
                    }
                } catch (e: IOException) {
                    Log.e("SyncWorker", "Everything IOException", e)
                    shouldRetry = true
                } catch (e: SQLException) {
                    Log.e("SyncWorker", "Everything DB error", e)
                    throw e // fail immediately
                } catch (e: Exception) {
                    Log.e("SyncWorker", "Everything unexpected error", e)
                }

                // -----------------------------
                // 3️⃣ Insert news if any
                // -----------------------------
                if (newsList.isNotEmpty()) {
                    repository.insertNews(newsList)
                }
            }

            // -----------------------------
            // 4️⃣ Decide Worker Result
            // -----------------------------
            return when {
                newsList.isNotEmpty() -> {
                    NotificationHelper.notifySync(applicationContext, "News Synced")
                    Log.d("SyncWorker", "Sync task completed")
                    Result.success()
                }
                shouldRetry -> {
                    Log.d("SyncWorker", "No news fetched, retrying due to network errors")
                    Result.retry()
                }
                else -> {
                    Log.e("SyncWorker", "No news fetched, failed due to permanent/unexpected errors")
                    Result.failure()
                }
            }
        } catch (e: IOException) {
            Log.e("SyncWorker", "Network error outside", e)
            return Result.retry()
        } catch (e: SQLException) {
            Log.e("SyncWorker", "Database error outside", e)
            return Result.failure()
        } catch (e: Exception) {
            Log.e("SyncWorker", "Unexpected error outside", e)
            return if (shouldRetry) Result.retry() else Result.failure()
        }
    }

}
