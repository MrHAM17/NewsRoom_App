package com.example.newsroom.data.remote.model.dto

import com.example.newsroom.data.local.room.entity.BookmarkEntity
import org.junit.Assert.*
import org.junit.Test

class ArticleBookmarkMapperTest {


    // ------------------------------
    // ArticleDto.toDomain()
    // ------------------------------

    @Test
    fun `toDomain returns Article and cleans content correctly`() {
        val dto = ArticleDto(
            source = SourceNameDto("s1", "CNN"),
            author = "Alice",
            title = "Breaking News",
            description = "Short desc",
            url = "https://example.com/article1",
            urlToImage = "https://example.com/img.png",
            publishedAt = "2025-09-18T12:00:00Z",
            content = "This is content [+123 chars]"
        )

        val article = dto.toDomain()

        assertNotNull(article)
        assertEquals("Breaking News", article!!.title)
        assertEquals("https://example.com/article1", article.url)
        assertEquals("https://example.com/img.png", article.imageUrl)
        // ensure regex cleaned the [+... chars] suffix and trimmed
        assertEquals("This is content", article.content)
    }

    @Test
    fun `toDomain returns null when title is missing`() {
        val dto = ArticleDto(
            source = null,
            author = null,
            title = null,
            description = null,
            url = "https://example.com",
            urlToImage = null,
            publishedAt = null,
            content = null
        )

        assertNull(dto.toDomain())
    }

    @Test
    fun `toDomain returns null when url is missing`() {
        val dto = ArticleDto(
            source = null,
            author = null,
            title = "Some title",
            description = null,
            url = null,
            urlToImage = null,
            publishedAt = null,
            content = null
        )

        assertNull(dto.toDomain())
    }


    // ------------------------------
    // BookmarkEntity.toArticle()
    // ------------------------------
    @Test
    fun `toArticle maps BookmarkEntity to Article correctly`() {
        val bookmark = BookmarkEntity(
            id = "123",
            sourceName = "CNN",
            title = "Test title",
            url = "https://example.com",
            imageUrl = "https://example.com/image.png"
        )

        val article = bookmark.toArticle()

        assertEquals("123", article.id)
        assertEquals("CNN", article.sourceName)
        assertEquals("Test title", article.title)
        assertEquals("https://example.com", article.url)
        assertEquals("https://example.com/image.png", article.imageUrl)

        // fields not stored in bookmarks should be null
        assertNull(article.author)
        assertNull(article.description)
        assertNull(article.publishedAtUtc)
        assertNull(article.content)
    }
}
