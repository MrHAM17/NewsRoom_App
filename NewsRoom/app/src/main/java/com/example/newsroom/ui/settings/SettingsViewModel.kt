package com.example.newsroom.ui.settings

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsroom.data.repository.NewsRepository
import com.example.newsroom.ui.settings.importExport.BookmarkExport
import com.example.newsroom.ui.settings.importExport.BookmarkImport
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.Result.Companion.success
@HiltViewModel
class SettingsViewModel @Inject constructor(
    app: Application,
    private val repo: NewsRepository
) : AndroidViewModel(app) {

    fun exportTo(uri: Uri, format: BookmarkExport.Format, onDone: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val ok = runCatching {
                val items = repo.getBookmarksToExport()
                BookmarkExport.write(getApplication<Application>().contentResolver, uri, items, format)
            }.isSuccess

            // Switch back to Main for UI work (like Toast)
            withContext(Dispatchers.Main) {
                onDone(ok) // pass true/false
            }
        }
    }

    fun importFrom(uri: Uri, onDone: (Int) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val cr = getApplication<Application>().contentResolver
            val list = runCatching { BookmarkImport.read(cr, uri) }.getOrElse { emptyList() }

//            if (list.isEmpty()) { onDone(0); return@launch }
            if (list.isNullOrEmpty()) { // ✅ check early
                withContext(Dispatchers.Main) { onDone(0) }
                return@launch
            }

            val normalized = list.map {
                // ensure uniqueness by URL if id missing
                if (it.id.isBlank()) it.copy(id = it.url) else it
            }

            // Insert or upsert into DB
            repo.getBookmarksToImport(normalized)
            val count = normalized.size


            withContext(Dispatchers.Main) {
                onDone(count) // return how many were imported
            }

        }
    }

    fun clearAllBookmarks(onDone: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val ok = runCatching { repo.removeAllBookmarks() }.isSuccess
            withContext(Dispatchers.Main) { onDone(ok) }
        }
    }

}