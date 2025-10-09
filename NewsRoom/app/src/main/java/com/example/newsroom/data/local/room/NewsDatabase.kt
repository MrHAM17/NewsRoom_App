package com.example.newsroom.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.newsroom.data.local.room.dao.NewsSyncAndSearchDao
import com.example.newsroom.data.local.room.dao.BookmarkDao
import com.example.newsroom.data.local.room.entity.NewsEntity
import com.example.newsroom.data.local.room.entity.BookmarkEntity
import com.example.newsroom.data.local.room.entity.NewsFtsEntity
import com.example.newsroom.data.local.room.entity.SearchHistoryEntity


// TODO: Add a migration test template if schema changes (v2 → v3, etc.)

@Database(
    entities = [
        BookmarkEntity::class,
        NewsEntity::class,
        NewsFtsEntity::class,
        SearchHistoryEntity::class
               ],
    version = 2,
    exportSchema = true) // true
abstract class NewsDatabase: RoomDatabase()  {
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun newsDao(): NewsSyncAndSearchDao

}
