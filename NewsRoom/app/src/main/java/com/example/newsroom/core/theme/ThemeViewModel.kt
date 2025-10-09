package com.example.newsroom.core.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val manager: ThemeManager
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = manager.themeFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, ThemeMode.SYSTEM)

    fun setTheme(mode: ThemeMode) {
        viewModelScope.launch {
            manager.setTheme(mode)
        }
    }
}
