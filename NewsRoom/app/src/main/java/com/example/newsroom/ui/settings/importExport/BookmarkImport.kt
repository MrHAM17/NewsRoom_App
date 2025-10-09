package com.example.newsroom.ui.settings.importExport

import android.content.ContentResolver
import android.net.Uri
import com.example.newsroom.data.local.room.entity.BookmarkEntity
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader

object BookmarkImport {

//    fun read(
//        cr: ContentResolver,
//        uri: Uri
//    ): List<BookmarkEntity> {
//        val name = cr.getType(uri) ?: ""
//        return if (name.contains("json") || uri.toString().endsWith(".json")) { // Also check file extension
//            readJson(cr, uri)
//        } else {
//            tryCsv(cr, uri)
//        }
//    }
    fun read(cr: ContentResolver, uri: Uri): List<BookmarkEntity> {
        return try {
            val name = cr.getType(uri) ?: ""
            if (name.contains("json") || uri.toString().endsWith(".json")) {
                readJson(cr, uri)
            } else {
                tryCsv(cr, uri)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList() // ✅ never return null
        }
    }

//    private fun readJson(cr: ContentResolver, uri: Uri): List<BookmarkEntity> {
//        val text = cr.openInputStream(uri)?.use { it.reader().readText() } ?: "[]"
//        if (text.isBlank()) return emptyList()
//
//        val arr = JSONArray(text)
//        val out = ArrayList<BookmarkEntity>(arr.length())
//        for (i in 0 until arr.length()) {
//            try {
//                val o = arr.getJSONObject(i)
//                out += BookmarkEntity(
//                    id = o.optString("id", "").takeIf { it.isNotBlank() } ?: o.optString("url", ""),
//                    title = o.optString("title", ""),
//                    url = o.optString("url", ""),
//                    sourceName = if (o.has("source") && !o.isNull("source")) o.optString("source") else null,
//                    imageUrl = if (o.has("image") && !o.isNull("image")) o.optString("image") else null,
//                    saveAt = o.optLong("savedAt", System.currentTimeMillis())
//                )
//            } catch (e: Exception) {
//                // Skip invalid entries but continue processing others
//                e.printStackTrace()
//            }
//        }
//        return out
//    }
    private fun readJson(cr: ContentResolver, uri: Uri): List<BookmarkEntity> {
        val text = cr.openInputStream(uri)?.use { it.reader().readText() } ?: "[]"
        if (text.isBlank()) return emptyList()

        return try {
            val arr = JSONArray(text)
            val out = ArrayList<BookmarkEntity>(arr.length())
            for (i in 0 until arr.length()) {
                try {
                    val o = arr.getJSONObject(i)
                    out += BookmarkEntity(
                        id = o.optString("id", "").takeIf { it.isNotBlank() } ?: o.optString("url", ""),
                        title = o.optString("title", ""),
                        url = o.optString("url", ""),
                        sourceName = if (o.has("source") && !o.isNull("source")) o.optString("source") else null,
                        imageUrl = if (o.has("image") && !o.isNull("image")) o.optString("image") else null,
                        saveAt = o.optLong("savedAt", System.currentTimeMillis())
                    )
                } catch (e: Exception) {
                    // Skip invalid entry but continue
                    e.printStackTrace()
                }
            }
            out
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList() // ✅ never return null
        }
    }


//    private fun tryCsv(cr: ContentResolver, uri: Uri): List<BookmarkEntity> {
//        val br = BufferedReader(InputStreamReader(cr.openInputStream(uri)!!))
//        val out = mutableListOf<BookmarkEntity>()
//        var first = true
//        br.useLines { lines ->
//            lines.forEach { line ->
//                if (first) { first = false; return@forEach } // skip header
//                if (line.isBlank()) return@forEach
//                val cols = parseCsvLine(line)
//                if (cols.size >= 3) { // Need at least id, title, url
//                    val id = cols.getOrNull(0)?.takeIf { it.isNotBlank() } ?: cols.getOrNull(2) ?: ""
//                    out += BookmarkEntity(
//                        id = id,
//                        title = cols.getOrNull(1) ?: "",
//                        url = cols.getOrNull(2) ?: "",
//                        sourceName = cols.getOrNull(3)?.takeIf { it.isNotBlank() },
//                        imageUrl = cols.getOrNull(4)?.takeIf { it.isNotBlank() },
//                        saveAt = cols.getOrNull(5)?.toLongOrNull() ?: System.currentTimeMillis()
//                    )
//                }
//            }
//        }
//        return out
//    }
    private fun tryCsv(cr: ContentResolver, uri: Uri): List<BookmarkEntity> {
        return try {
            val br = BufferedReader(InputStreamReader(cr.openInputStream(uri)!!))
            val out = mutableListOf<BookmarkEntity>()
            var first = true
            br.useLines { lines ->
                lines.forEach { line ->
                    if (first) { first = false; return@forEach } // skip header
                    if (line.isBlank()) return@forEach
                    val cols = parseCsvLine(line)
                    if (cols.size >= 3) { // Need at least id, title, url
                        val id = cols.getOrNull(0)?.takeIf { it.isNotBlank() } ?: cols.getOrNull(2) ?: ""
                        out += BookmarkEntity(
                            id = id,
                            title = cols.getOrNull(1) ?: "",
                            url = cols.getOrNull(2) ?: "",
                            sourceName = cols.getOrNull(3)?.takeIf { it.isNotBlank() },
                            imageUrl = cols.getOrNull(4)?.takeIf { it.isNotBlank() },
                            saveAt = cols.getOrNull(5)?.toLongOrNull() ?: System.currentTimeMillis()
                        )
                    }
                }
            }
            out
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList() // ✅ never return null
        }
    }


    // naive CSV split that respects quotes
    private fun parseCsvLine(line: String): List<String> {
        val out = mutableListOf<String>()
        val sb = StringBuilder()
        var inQ = false
        var i = 0
        while (i < line.length) {
            val c = line[i]
            if (c == '"') {
                if (inQ && i + 1 < line.length && line[i + 1] == '"') { // escaped "
                    sb.append('"'); i++
                } else inQ = !inQ
            } else if (c == ',' && !inQ) {
                out += sb.toString(); sb.clear()
            } else sb.append(c)
            i++
        }
        out += sb.toString()
        return out
    }
}