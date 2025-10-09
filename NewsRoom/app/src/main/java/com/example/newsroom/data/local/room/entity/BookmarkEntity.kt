package com.example.newsroom.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(

    @PrimaryKey
    val id: String, // same as Article.id (URL)

    val title: String,
    val url: String,
    val sourceName: String?,
    val imageUrl: String?,
    val saveAt: Long = System.currentTimeMillis()
)
