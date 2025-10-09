package com.example.newsroom.ui.settings.importExport

import android.net.Uri
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.newsroom.databinding.FragmentSettingsBinding
import com.example.newsroom.ui.settings.SettingsViewModel

/**
 * Helper to attach import/export bookmark logic to SettingsFragment UI.
 */
class BookmarksImportExportHandler(
    private val fragment: Fragment,
    private val vm: SettingsViewModel,
    private val binding: FragmentSettingsBinding
) {
    private var fmt = BookmarkExport.Format.CSV

    // launchers must be registered with fragment
    private val createFile: ActivityResultLauncher<String> =
        fragment.registerForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri: Uri? ->
            uri ?: return@registerForActivityResult
            vm.exportTo(uri, fmt) { ok ->
                Toast.makeText(fragment.requireContext(), if (ok) "Exported" else "Failed", Toast.LENGTH_SHORT).show()
            }
        }

    // Launcher for JSON export
    private val createJson: ActivityResultLauncher<String> =
        fragment.registerForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri: Uri? ->
            uri ?: return@registerForActivityResult
            vm.exportTo(uri, BookmarkExport.Format.JSON) { ok ->
                Toast.makeText(fragment.requireContext(), if (ok) "Exported" else "Failed", Toast.LENGTH_SHORT).show()
            }
        }


    private val openFile: ActivityResultLauncher<Array<String>> =
        fragment.registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri ?: return@registerForActivityResult
            vm.importFrom(uri) { count ->
//                Toast.makeText(fragment.requireContext(), "Imported $count bookmarks", Toast.LENGTH_SHORT).show()
                val msg = if (count > 0) "Imported $count bookmarks"
                else "No bookmarks found in file"
                Toast.makeText(fragment.requireContext(), msg, Toast.LENGTH_SHORT).show()
            }
        }

    fun setup() {
        // Format selector
        binding.spinnerFormat.adapter = ArrayAdapter(
            fragment.requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            listOf("CSV", "JSON")
        )
        binding.spinnerFormat.setSelection(0)
        binding.spinnerFormat.setOnItemSelectedListener { pos ->
            fmt = if (pos == 0) BookmarkExport.Format.CSV else BookmarkExport.Format.JSON
        }

        // Export button
        binding.btnExport.setOnClickListener {
            when (fmt) {
                BookmarkExport.Format.CSV -> createFile.launch("bookmarks.csv")
                BookmarkExport.Format.JSON -> createJson.launch("bookmarks.json")
            }
        }

        // Import button
        binding.btnImport.setOnClickListener {
            openFile.launch(arrayOf("text/*", "application/json"))
        }
    }
}

// tiny extension to Spinner for brevity
private fun android.widget.Spinner.setOnItemSelectedListener(on: (Int) -> Unit) {
    onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
        override fun onItemSelected(p: android.widget.AdapterView<*>?, v: android.view.View?, pos: Int, id: Long) {
            on(pos)
        }
        override fun onNothingSelected(p: android.widget.AdapterView<*>?) {}
    }
}
