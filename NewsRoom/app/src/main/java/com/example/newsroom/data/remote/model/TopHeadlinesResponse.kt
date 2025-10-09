package com.example.newsroom.data.remote.model

import com.example.newsroom.data.remote.model.dto.ArticleDto
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class TopHeadlinesResponse(
    val status: String?,
    val totalResults: Int?,
    val articles: List<ArticleDto>?
)
