package com.example.newsroom.ui.home.news

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsroom.data.remote.model.dto.Article
import com.example.newsroom.data.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NewsUiState(
    val loading: Boolean = false,
    val articles: List<Article> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class NewsListViewModel @Inject constructor(
    private val repo: NewsRepository
) : ViewModel() {

//    fun loadTopHeadlines(category: String) : Flow<NewsUiState> {
//        val state = MutableStateFlow(NewsUiState(loading = true))
//        viewModelScope.launch {
//            repo.topHeadlines(category = category).collect { result ->
//                state.value = result.fold(
//                    onSuccess = { NewsUiState(articles = it) },
//                    onFailure = { NewsUiState(error = it.message) }
//                )
//            }
//        }
//        return state
//    }

    private val _state = MutableStateFlow(NewsUiState())
    val state: Flow<NewsUiState> get() = _state

    fun loadTopHeadlines(category: String, ctx: Context) {

//        Log.d("NewsViewModel", "Loading headlines for $category") // ✅ Log before API call

        _state.value = NewsUiState(loading = true)
        viewModelScope.launch {

//            // Use safeNetworkCall just to check network first
            val networkResult = repo.safeNetworkCall(ctx) { /* dummy, just check */ }


            // If network is okay, continue with your normal Flow
            repo.topHeadlines(category)
                .catch { throwable ->
//                    _state.value = NewsUiState(error = throwable.message)  // ⚠️ Catches flow-level exceptions

                    // Flow-level errors
                    val errorMsg = if (networkResult.isFailure) networkResult.exceptionOrNull()?.message else throwable.message
                    _state.value = NewsUiState(error = errorMsg)
                }
                .collect { result ->
//                _state.value = result.fold(
//                    onSuccess = { NewsUiState(articles = it) },
//                    onFailure = { NewsUiState(error = it.message) }
//                )
                result.fold(
                    onSuccess = {
//                        Log.d("NewsViewModel", "Articles count: ${it.size}") // ✅ Log when successful
//                        _state.value = NewsUiState(articles = it)

                            articles ->
                        val errorMsg = if (networkResult.isFailure) { networkResult.exceptionOrNull()?.message } else null
                        _state.value = NewsUiState( articles = articles, error = errorMsg )


                    },   // ✅ API worked
                    onFailure = {
//                        Log.d("@JsonClass(generateAdapter = true)", "Error: ${it.message}") // ✅ Optional: Log on failure
//                        _state.value = NewsUiState(error = it.message)

                            throwable ->
                        val errorMsg = if (networkResult.isFailure) { networkResult.exceptionOrNull()?.message } else throwable.message
                        _state.value = NewsUiState(error = errorMsg)

                    }   // ⚠️ API returned failure
                )
            }
        }
    }

}
