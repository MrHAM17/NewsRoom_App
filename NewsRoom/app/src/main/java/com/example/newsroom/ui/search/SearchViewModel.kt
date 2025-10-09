package com.example.newsroom.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsroom.data.remote.model.dto.Article
import com.example.newsroom.data.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Use SearchUiState consistently
data class SearchUiState(
    val loading: Boolean = false,
    val articles: List<Article> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repo: NewsRepository
) : ViewModel() {

    fun searchEverything(query: String): Flow<SearchUiState> = flow {

        if (query.isEmpty()) {
            emit(SearchUiState(articles = emptyList()))
            return@flow
        }

        emit(SearchUiState(loading = true))
        try {
            repo.everything(query).collect { result ->
                result.fold(
                    onSuccess = { articles ->
                        // Save query in DB
                        viewModelScope.launch { repo.insertSearchQuery(query) }
                        emit(SearchUiState(loading = false, articles = articles))
                    },
                    onFailure = { error ->
                        // fallback to FTS
                        val rows = repo.ftsSearch(query)
                        val articles = rows.map {
                            Article(
                                id = it.url,
                                sourceId = null,
                                sourceName = null,
                                author = null,
                                title = it.title,
                                description = it.snippet,
                                url = it.url,
                                imageUrl = null,
                                publishedAtUtc = null,
                                content = null
                            )
                        }
                        emit(SearchUiState(loading = false, articles = articles, error = error.message))
                    }
                )
            }
        } catch (t: Throwable) {
            val rows = repo.ftsSearch(query)
            val articles = rows.map {
                Article(it.url, null, null, null, it.title, it.snippet, it.url, null, null, null)
            }
            emit(SearchUiState(loading = false, articles = articles, error = t.message))
        }
    }
}
