package com.example.newsroom.ui.settings.importExport

import android.content.ContentResolver
import android.net.Uri
import com.example.newsroom.data.local.room.entity.BookmarkEntity
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.OutputStreamWriter

object BookmarkExport {

    enum class Format { CSV, JSON }

    fun write(
        cr: ContentResolver,
        uri: Uri,
        bookmarks: List<BookmarkEntity>,
        format: Format
    ) {
        cr.openOutputStream(uri)?.use { os ->
            when (format) {
                Format.CSV -> BufferedWriter(OutputStreamWriter(os)).use { w ->
                    w.appendLine("id,title,url,source,image,savedAt")
                    bookmarks.forEach { b ->
                        w.appendLine(
                            listOf(b.id, b.title, b.url, b.sourceName ?: "", b.imageUrl ?: "", b.saveAt.toString())
                                .joinToString(",") { csvEscape(it) }
                        )
                    }
                }
                Format.JSON -> BufferedWriter(OutputStreamWriter(os)).use { w -> // Fix: Use BufferedWriter for JSON too
                    val arr = JSONArray()
                    bookmarks.forEach { b ->
                        arr.put(JSONObject().apply {
                            put("id", b.id)
                            put("title", b.title)
                            put("url", b.url)
                            put("source", b.sourceName ?: JSONObject.NULL) // Use NULL for null values
                            put("image", b.imageUrl ?: JSONObject.NULL)
                            put("savedAt", b.saveAt)
                        })
                    }
                    w.write(arr.toString(2))
                    w.flush()  // ← Explicitly flush the writer

//                    // Write directly to OutputStream like CSV does
//                    os.write(arr.toString(2).toByteArray())
//                    os.flush()  // ← Ensure the stream is flushed
                }
            }
        } ?: error("Unable to open OutputStream")
    }

    private fun csvEscape(s: String?): String {
        val t = (s ?: "").replace("\"", "\"\"")
        return if (t.contains(',') || t.contains('\n') || t.contains('\r')) "\"$t\"" else t
    }
}