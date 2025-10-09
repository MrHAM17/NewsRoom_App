package com.example.newsroom.core.di

import com.example.newsroom.data.repository.NewsRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun newsRepository(): NewsRepository
}
