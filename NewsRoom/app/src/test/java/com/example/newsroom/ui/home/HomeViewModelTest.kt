package com.example.newsroom.ui.home

import androidx.lifecycle.SavedStateHandle
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel
    private lateinit var savedStateHandle: SavedStateHandle

    @Before
    fun setup() {
        savedStateHandle = SavedStateHandle()
        viewModel = HomeViewModel(savedStateHandle)
    }

    @Test
    fun `getLastCategory returns default if nothing saved`() {
        val default = "general"
        assertEquals(default, viewModel.getLastCategory())
    }

    @Test
    fun `setLastCategory updates saved state and getLastCategory returns it`() {
        val category = "science"
        viewModel.setLastCategory(category)
        assertEquals(category, viewModel.getLastCategory())
    }
}
