package com.example.newsroom.data.remote.model

import com.example.newsroom.data.remote.model.dto.ArticleDto

data class EverythingResponse(
    val status: String?,
    val totalResults: Int?,
    val articles: List<ArticleDto>?
)
