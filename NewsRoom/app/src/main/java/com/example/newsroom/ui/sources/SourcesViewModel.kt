package com.example.newsroom.ui.sources

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsroom.data.remote.model.dto.SourceDto
import com.example.newsroom.data.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SourcesState(
    val loading: Boolean = true ,
    val list: List<SourceDto> = emptyList(),
    val error: String? = null
)


@HiltViewModel
class SourcesViewModel @Inject constructor
    (private val repo: NewsRepository): ViewModel() {

    private val _state = MutableStateFlow(SourcesState())
    val state: StateFlow<SourcesState> = _state

    fun load(category: String? = null) {
        viewModelScope.launch {
            try {
                _state.value = SourcesState(loading = true)
                val res = repo.sources(category)
                _state.value = SourcesState(loading = false,list = res.sources)
            }
            catch (t: Throwable) {
                _state.value = SourcesState(loading = false, error = t.message)
            }
        }
    }

}