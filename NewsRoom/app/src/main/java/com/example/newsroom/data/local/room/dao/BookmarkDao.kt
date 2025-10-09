package com.example.newsroom.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.newsroom.data.local.room.entity.BookmarkEntity
import kotlinx.coroutines.flow.Flow
import kotlin.OptIn

@Dao
interface
BookmarkDao {

    @Query("SELECT * FROM bookmarks ORDER BY saveAt DESC")
    fun observeBookmarks(): Flow<List<BookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM bookmarks")
    suspend fun deleteAllBookmarks()

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE id = :id)")
    suspend fun isBookmarked(id: String): Boolean


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAllForImport(bookmarks: List<BookmarkEntity>) // for Import

    @Query("SELECT * FROM bookmarks ORDER BY saveAt DESC")
    suspend fun getAllForExport(): List<BookmarkEntity>

}