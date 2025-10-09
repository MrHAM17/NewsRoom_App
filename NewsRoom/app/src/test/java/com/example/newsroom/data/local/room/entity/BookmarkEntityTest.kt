package com.example.newsroom.data.local.room.entity

import com.example.newsroom.data.remote.model.dto.toArticle
import org.junit.Assert.assertEquals
import org.junit.Test

// NOTE: This file's package depends on your project's packages.
// The BookmarkEntity data class in your code is at:
// com.example.newsroom.data.local.room.entity.BookmarkEntity
// Ensure the package above matches that.

class BookmarkEntityTest {

    @Test
    fun `toArticle maps fields correctly`() {
        val savedAt = 123456789L
        val be = com.example.newsroom.data.local.room.entity.BookmarkEntity(
            id = "https://example.com/1",
            title = "Title 1",
            url = "https://example.com/1",
            sourceName = "CNN",
            imageUrl = "https://example.com/img.png",
            saveAt = savedAt
        )

        val article = be.toArticle()
        assertEquals(be.id, article.id)
        assertEquals(be.title, article.title)
        assertEquals(be.url, article.url)
        assertEquals(be.sourceName, article.sourceName)
        assertEquals(be.imageUrl, article.imageUrl)
        // fields intentionally null in toArticle
        assertEquals(null, article.author)
        assertEquals(null, article.content)
    }
}
