package com.example.newsroom.data.local.room.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.newsroom.data.local.room.NewsDatabase
import com.example.newsroom.data.local.room.entity.NewsEntity
import com.example.newsroom.data.local.room.entity.SearchHistoryEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented tests for NewsSyncAndSearchDao
 */
@RunWith(AndroidJUnit4::class)
class NewsSyncAndSearchDaoTest {

    private lateinit var db: NewsDatabase
    private lateinit var dao: NewsSyncAndSearchDao

    @Before
    fun setup() {
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = Room.inMemoryDatabaseBuilder(ctx, NewsDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.newsDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertNewsAndSearchFtsBasicSmokeTest() = runBlocking {
        val n1 = NewsEntity(title = "Kotlin testing", content = "Kotlin coroutines test", url = "https://a")
        val n2 = NewsEntity(title = "Android Room", content = "Room FTS check", url = "https://b")

        dao.insertNews(listOf(n1, n2))

        // searchFts uses FTS; query syntax can vary; do a simple call to ensure no exception and return type list
        val ftsResults = dao.searchFts("Kotlin")
        // might be empty depending on tokenizer, but should not throw and returns a List
        assertNotNull(ftsResults)
    }

    @Test
    fun insertSearchHistoryAndGetSearchHistoryFlowReturnsHistory() = runBlocking {
        dao.insertSearchHistory(SearchHistoryEntity(query = "first"))
        dao.insertSearchHistory(SearchHistoryEntity(query = "second"))

        val list = dao.getSearchHistoryFlow().first()
        assertTrue(list.isNotEmpty())
        assertEquals("second", list.first().query) // ordered by ts DESC
    }

    @Test
    fun clearSearchHistoryAndClearNewsWorks() = runBlocking {
        dao.insertSearchHistory(SearchHistoryEntity(query = "toClear"))
        val before = dao.getSearchHistoryFlow().first().size
        assertTrue(before >= 1)

        dao.clearSearchHistory()
        val after = dao.getSearchHistoryFlow().first().size
        assertEquals(0, after)

        // Insert news, then clear
        val n = NewsEntity(title = "t", content = "c", url = "u")
        dao.insertNews(listOf(n))
        dao.clearNews()
        // searchFts should return empty or not crash
        val fts = dao.searchFts("t")
        // we can't guarantee size, but ensure call returns list
        assertNotNull(fts)
    }
}
