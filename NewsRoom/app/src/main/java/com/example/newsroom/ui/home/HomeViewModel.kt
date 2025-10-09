package com.example.newsroom.ui.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.newsroom.data.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor ( private val savedState: SavedStateHandle ) : ViewModel()
{
    companion object {
        private const val  KEY_LAST_CATEGORY = "last_category"
    }

    fun setLastCategory(category: String) { savedState[KEY_LAST_CATEGORY] = category }

    fun getLastCategory(default: String = "general") = savedState.get<String>(KEY_LAST_CATEGORY) ?: default

}