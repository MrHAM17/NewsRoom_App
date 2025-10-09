package com.example.newsroom.data.local.room.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.newsroom.data.local.room.NewsDatabase
import com.example.newsroom.data.local.room.entity.BookmarkEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BookmarkDaoTest {

    private lateinit var db: NewsDatabase
    private lateinit var dao: BookmarkDao

    @Before
    fun setup() {
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(ctx, NewsDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.bookmarkDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insertAndIsBookmarked() = runBlocking {
        val b = BookmarkEntity(
            id = "id-1",
            title = "Title 1",
            url = "https://example.com/1",
            sourceName = "Source",
            imageUrl = null
        )
        dao.upsert(b)

        val exists = dao.isBookmarked("id-1")
        Assert.assertTrue(exists)
    }
}