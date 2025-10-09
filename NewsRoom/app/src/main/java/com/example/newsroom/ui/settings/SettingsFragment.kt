package com.example.newsroom.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.newsroom.R
import com.example.newsroom.core.theme.ThemeMode
import com.example.newsroom.databinding.FragmentSettingsBinding
import com.example.newsroom.ui.settings.importExport.BookmarksImportExportHandler
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import com.example.newsroom.core.theme.ThemeViewModel

@AndroidEntryPoint
class SettingsFragment: Fragment() {

    private var _b: FragmentSettingsBinding? = null
    private val b get() = _b!!
    private val settingsVm: SettingsViewModel by viewModels()
    private val themeVm: ThemeViewModel by viewModels()

    private lateinit var bookmarksHandler: BookmarksImportExportHandler

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _b = FragmentSettingsBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // theme
        // Observe current theme
        lifecycleScope.launchWhenStarted {
            themeVm.themeMode.collect { mode ->
                when (mode) {
                    ThemeMode.SYSTEM -> b.rbSystem.isChecked = true
                    ThemeMode.LIGHT -> b.rbLight.isChecked = true
                    ThemeMode.DARK -> b.rbDark.isChecked = true
                }
            }
        }
        // Handle clicks
        b.rbSystem.setOnClickListener { themeVm.setTheme(ThemeMode.SYSTEM) }
        b.rbLight.setOnClickListener { themeVm.setTheme(ThemeMode.LIGHT) }
        b.rbDark.setOnClickListener { themeVm.setTheme(ThemeMode.DARK) }


        // init handler
        bookmarksHandler = BookmarksImportExportHandler(this, settingsVm, b)
        bookmarksHandler.setup()


        // delete all bookmarks
        b.btnDeleteAllBookmarks.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.delete_all_bookmarks_title)
                .setMessage(R.string.delete_all_bookmarks_message)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete) { _, _ ->
                    settingsVm.clearAllBookmarks { ok ->
                        if (ok) { Snackbar.make(b.root, R.string.all_bookmarks_deleted, Snackbar.LENGTH_SHORT).show()  }
                        else { Snackbar.make(b.root, R.string.failed_to_delete_bookmarks, Snackbar.LENGTH_SHORT).show()  }
                    }
                }.show()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
