package com.example.newsroom.ui.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsroom.data.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SavedViewModel @Inject constructor(
    private val repo: NewsRepository
) : ViewModel() {

    val bookmarks = repo.observeBookmarks()
        .stateIn(viewModelScope,
            SharingStarted.Eagerly, // previous --> SharingStarted.Lazily,
            emptyList())
}


