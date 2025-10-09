package com.example.newsroom.data.remote.model.dto

import com.example.newsroom.data.local.room.entity.BookmarkEntity

/*

The app calls the API using EverythingResponse or .., which gives raw ArticleDto data.
A mapper (toDomain()) transforms each ArticleDto into a clean Article.
The app uses this cleaned Article list in the UI or ViewModel.

Clean Architecture principle:
Never let your app’s UI or logic layer directly depend on raw DTOs. Always map to domain models.

*/

data class Article(
    val id: String,              // derived stable id (url or hash)
    val sourceId: String?,
    val sourceName: String?,
    val author: String?,
    val title: String,
    val description: String?,
    val url: String,
    val imageUrl: String?,
    val publishedAtUtc: String?,
    val content: String?
)

fun ArticleDto.toDomain(): Article? {
    val titleSafe = title ?: return null
    val urlSafe = url ?: return null
    val cleanContent = content?.replace(Regex("\\[\\+\\d+ chars]"), "")?.trim()

    return Article(
        id = urlSafe, // simple: use URL as unique id
        sourceId = source?.id,
        sourceName = source?.name,
        author = author,
        title = titleSafe,
        description = description,
        url = urlSafe,
        imageUrl = urlToImage,
        publishedAtUtc = publishedAt,
//        content = content
        content = cleanContent   // ✅ cleaned

    )
}

fun BookmarkEntity.toArticle(): Article = Article(
    id = id,
    sourceId = null, // not stored in bookmarks
    sourceName = sourceName,
    author = null, // not stored
    title = title,
    description = null, // not stored
    url = url,
    imageUrl = imageUrl,
    publishedAtUtc = null, // not stored
    content = null
)
