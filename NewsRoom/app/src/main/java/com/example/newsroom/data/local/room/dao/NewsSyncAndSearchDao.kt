package com.example.newsroom.data.local.room.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.newsroom.data.local.room.entity.NewsEntity
import com.example.newsroom.data.local.room.entity.NewsFtsEntity
import com.example.newsroom.data.local.room.entity.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsSyncAndSearchDao {

    // Insert into normal table
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(news: List<NewsEntity>)

    // Insert search history
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSearchHistory(history: SearchHistoryEntity)

    // FTS search query
    @Query("SELECT * FROM news_fts WHERE news_fts MATCH :query LIMIT :limit")
    suspend fun searchFts(query: String, limit: Int = 50): List<NewsFtsEntity>

    @Query("SELECT * FROM search_history ORDER BY ts DESC")
    fun getSearchHistoryFlow(): Flow<List<SearchHistoryEntity>>

    @Query("DELETE FROM search_history")
    suspend fun clearSearchHistory()

    @Query("DELETE FROM news")
    suspend fun clearNews()

}