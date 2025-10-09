package com.example.newsroom.data.repository

import android.content.Context
import com.example.newsroom.data.local.room.NewsDatabase
import com.example.newsroom.data.local.room.entity.BookmarkEntity
import com.example.newsroom.data.local.room.entity.FtsRow
import com.example.newsroom.data.local.room.entity.NewsEntity
import com.example.newsroom.data.local.room.entity.SearchHistoryEntity
import com.example.newsroom.data.remote.api.NewsApiService
import com.example.newsroom.data.remote.model.dto.Article
import com.example.newsroom.data.remote.model.dto.toDomain
import com.example.newsroom.core.util.network.getNetworkStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class NewsRepository @Inject constructor (private val api: NewsApiService, private val db: NewsDatabase ) {

    // --- Network (Remote-Retrofit) ---

    suspend fun <T> safeNetworkCall(
        context: Context,
        networkBlock: suspend () -> T
    ): Result<T> {
        val status = getNetworkStatus(context)

        if (!status.wifi && !status.mobile && !status.ethernet)  {
            return Result.failure(Exception("No network connection.. & no cached data is available to show here."))
        } else if (!status.internet) {
            return Result.failure(Exception("No internet.. & no cached data is available to show here."))
        }

        return try {
            Result.success(networkBlock())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun topHeadlines(category: String, country: String? = "us", pageSize: Int = 20, page: Int = 1 )
    : Flow<Result<List<Article>>> = flow {
        try {
            val response = api.getTopHeadlines(country = country, category = category, pageSize = pageSize, page = page)
//            val list = response.articles!!.mapNotNull { it.toDomain() }
            val list = response.articles.orEmpty().mapNotNull { it.toDomain() }
            emit(Result.success(list))
        }
        catch (t: Throwable) {
            emit(Result.failure(t))
        }
    }.flowOn(Dispatchers.IO)    // ✅ run network + mapping on background thread

    fun everything(query: String, page: Int = 1)
    : Flow<Result<List<Article>>> = flow {
        try {
            val response = api.getEverything(query, page = page, pageSize = 20)
            val list = response.articles!!.mapNotNull { it.toDomain() }
            emit(Result.success(list))
        }
        catch (t: Throwable) {
            emit(Result.failure(t))
        }
    }.flowOn(Dispatchers.IO)    // ✅ run network + mapping on background thread

    suspend fun sources(category: String? = null) = api.getSources(category = category)




    // --- Bookmarks (Local-Room) ---
    fun observeBookmarks() = db.bookmarkDao().observeBookmarks()

    suspend fun saveBookmark(article: Article) {
        db.bookmarkDao().upsert(
            BookmarkEntity(
                id = article.id,
                title = article.title,
                url = article.url,
                sourceName = article.sourceName,
                imageUrl = article.imageUrl
            )
        )
    }

    suspend fun removeBookmark(id: String) = db.bookmarkDao().deleteById(id)

    suspend fun removeAllBookmarks() = db.bookmarkDao().deleteAllBookmarks()

    suspend fun isBookmaarked(id: String) = db.bookmarkDao().isBookmarked(id)

    suspend fun getBookmarksToImport (bookmarks: List<BookmarkEntity>) {
        db.bookmarkDao().upsertAllForImport(bookmarks)
    }
    suspend fun getBookmarksToExport (): List<BookmarkEntity> {
        return db.bookmarkDao().getAllForExport()
    }




    // --- Sync & Search (Remote & Local) ---
    // In NewsRepository
    suspend fun insertNews(newsList: List<NewsEntity>) {
        db.newsDao().insertNews(newsList)
    }

    suspend fun insertSearchQuery(query: String) {
        db.newsDao().insertSearchHistory(SearchHistoryEntity(query = query))
    }

    suspend fun ftsSearch(query: String, limit: Int = 50): List<FtsRow> {
        return db.newsDao().searchFts(query, limit).map {
            FtsRow(it.title, it.content.take(200), it.url)
        }
    }

    // Observe search history (optional, flow)
    fun observeSearchHistory(): Flow<List<SearchHistoryEntity>> {
        return db.newsDao().getSearchHistoryFlow()
    }

    // Clear old search history
    suspend fun clearSearchHistory() {
        db.newsDao().clearSearchHistory()
    }

    // Clear old all news data
    suspend fun clearAll() = db.newsDao().clearNews()

}
