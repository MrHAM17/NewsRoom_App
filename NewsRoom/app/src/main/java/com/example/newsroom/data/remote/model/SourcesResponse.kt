package com.example.newsroom.data.remote.model

import com.example.newsroom.data.remote.model.dto.SourceDto
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SourcesResponse(
    val status: String?,
    val sources: List<SourceDto>
)


