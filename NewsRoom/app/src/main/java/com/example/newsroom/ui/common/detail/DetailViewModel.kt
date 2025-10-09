package com.example.newsroom.ui.common.detail


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.newsroom.data.remote.model.dto.Article
import com.example.newsroom.data.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor
    (private val repo: NewsRepository): ViewModel() {

//    fun observeBookmarkState(id: String): LiveData<Boolean> = liveData {
//        emit(repo.isBookmaarked(id))
//
//        // (could also expose Flow for realtime; simple for now)
//    }


    private val _isBookmarked = MutableLiveData<Boolean>()
    val isBookmarked: LiveData<Boolean> = _isBookmarked

    private val _bookmarkRemoved = MutableLiveData<Boolean>()
    val bookmarkRemoved: LiveData<Boolean> = _bookmarkRemoved


    fun checkIsBookmarked(id: String) {
        viewModelScope.launch {
            try {
                // Run DB check on IO thread
                val bookmarked = withContext(Dispatchers.IO) {
                    repo.isBookmaarked(id)  // returns true/false
                }
                _isBookmarked.value = bookmarked     // Update LiveData on main thread
            } catch (e: Exception) {
                // Optional: show error
            }
        }
    }


    fun saveBookmark(id: String, title: String, url: String, source: String?, image: String?) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    repo.saveBookmark(Article(id, null, source, null, title, null, url, image, null, null))
                }
                _isBookmarked.value = true   // Immediately reflect in UI
            } catch (e: Exception) {
                // Optional: show error
            }
        }
    }


    fun removeBookmark(id: String) {
        viewModelScope.launch {
            try {
                // Run DB call on IO thread
                withContext(Dispatchers.IO) {
                    repo.removeBookmark(id)
                }
                // Update LiveData on main thread
                _isBookmarked.value = false
                _bookmarkRemoved.value = true
            } catch (e: Exception) {
                // Optional: show error message
            }
        }
    }

}