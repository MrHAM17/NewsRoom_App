package com.example.newsroom

import com.example.newsroom.data.local.room.entity.BookmarkEntity
import com.example.newsroom.data.remote.model.SourcesResponse
import com.example.newsroom.data.remote.model.dto.Article
import com.example.newsroom.data.remote.model.dto.SourceDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeNewsRepository  {

    // default fake articles
    private val fakeArticles = listOf(
        Article(
            id = "1",
            title = "Test Title 1",
            description = "Test Description 1",
            content = "Test Content 1",
            author = "Author 1",
            imageUrl = "https://example.com/image1.jpg",
            url = "https://example.com/article1",
            publishedAtUtc = "2025-09-18T10:00:00Z",
            sourceId = "source1",
            sourceName = "Test Source 1"
        ),
        Article(
            id = "2",
            title = "Test Title 2",
            description = "Test Description 2",
            content = "Test Content 2",
            author = "Author 2",
            imageUrl = "https://example.com/image2.jpg",
            url = "https://example.com/article2",
            publishedAtUtc = "2025-09-17T15:00:00Z",
            sourceId = "source2",
            sourceName = "Test Source 2"
        )
    )
    // default fake bookmarks
    private val fakeBookmarks = listOf(
        BookmarkEntity(
            id = "1",
            title = "Saved Article 1",
            url = "https://example.com/1",
            sourceName = "Source 1",
            imageUrl = "https://example.com/image1.jpg"
        ),
        BookmarkEntity(
            id = "2",
            title = "Saved Article 2",
            url = "https://example.com/2",
            sourceName = "Source 2",
            imageUrl = "https://example.com/image2.jpg"
        )
    )
    // Default fake sources
    private val fakeSources = listOf(
        SourceDto(
            id = "source1",
            name = "Source One",
            description = "Description One",
            url = "https://example.com/1",
            category = null,
            language = null,
            country = null
        ),
        SourceDto(
            id = "source2",
            name = "Source Two",
            description = "Description Two",
            url = "https://example.com/2",
            category = null,
            language = null,
            country = null
        )
    )


    // Mutable state → you control these in tests
    private var currentBookmarks: List<BookmarkEntity> = fakeBookmarks
    private var currentNews: List<Article> = fakeArticles
    // For SearchFragment tests
    private var currentSearchResults: List<Article> = emptyList()
    private var currentFtsResults: List<Article> = emptyList()
    private var forceError: Boolean = false
    // For SourcesFragment tests
    private var currentSources: List<SourceDto> = fakeSources




    // ---- Helpers for tests ----
    fun overrideBookmarks(newBookmarks: List<BookmarkEntity>) { currentBookmarks = newBookmarks  }
    fun overrideNews(newNews: List<Article>) {  currentNews = newNews  }
    fun setSearchResults(list: List<Article>) { currentSearchResults = list }
    fun setFtsResults(list: List<Article>) { currentFtsResults = list }
    fun setErrorMode(value: Boolean) { forceError = value }
    // For SourcesFragment tests
    fun overrideSources(newSources: List<SourceDto>) { currentSources = newSources }



    // --- Get defaults for assertions ---
    // inside FakeNewsRepository
    fun getDefaultArticles(): List<Article> = fakeArticles
    fun getDefaultBookmarks(): List<BookmarkEntity> = fakeBookmarks




    // Flows exposed to Fragments/ViewModels
    fun observeBookmarks(): Flow<List<BookmarkEntity>> = flow { emit(currentBookmarks) }
    fun getNews(): Flow<List<Article>> = flow { emit(currentNews) }
    fun search(query: String): Flow<List<Article>> = flow {
        if (forceError)
        { emit(currentFtsResults) }     // offline fallback
        else
        {  emit(currentSearchResults)  }
    }
    // --- Sources support ---
    suspend fun sources(category: String? = null): SourcesResponse {
        if (forceError) throw Exception("Network error") // simulate offline/failure
        return SourcesResponse(status = "ok", sources = currentSources)
    }
}
