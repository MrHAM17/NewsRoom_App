package com.example.newsroom.data.local.room.entity


import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

// Regular news table
@Entity(tableName = "news")
data class NewsEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,
    val content: String?,   // ✅ allow null
    val url: String
)


// FTS virtual table for full-text search
@Fts4(contentEntity = NewsEntity::class)
@Entity(tableName = "news_fts")
data class NewsFtsEntity(
    val title: String,
    val content: String,
    val url: String

)


// Search history
@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val query: String,
    val ts: Long = System.currentTimeMillis()
)



// Fts results
data class FtsRow(
    val title: String,
    val snippet: String,
    val url: String
)
