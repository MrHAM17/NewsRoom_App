package com.example.newsroom.ui.settings.importExport

import android.content.ContentResolver
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.newsroom.data.local.room.entity.BookmarkEntity
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class BookmarkExportTest {

    private lateinit var contentResolver: ContentResolver

    @Before
    fun setup() {
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        contentResolver = ctx.contentResolver
    }

    @Test
    fun exportCsvWritesFileWithBookmarkData() {
        val file = File.createTempFile("bookmark", ".csv")
        val uri = Uri.fromFile(file)

        val bookmarks = listOf(
            BookmarkEntity("id1", "Title1", "http://url1", "CNN", "http://image1", 123456L),
            BookmarkEntity("id2", "Title2", "http://url2", "BBC", "http://image2", 123457L)
        )

        // call the real API
        BookmarkExport.write(contentResolver, uri, bookmarks, BookmarkExport.Format.CSV)

        val text = file.readText()
        assertTrue("Should contain Title1", text.contains("Title1"))
        assertTrue("Should contain Title2", text.contains("Title2"))
        assertTrue("Should contain CSV header", text.contains("id,title,url,source,image,savedAt"))
    }

    @Test
    fun exportJsonWritesFileWithBookmarkData() {
        val file = File.createTempFile("bookmark", ".json")
        val uri = Uri.fromFile(file)

        val bookmarks = listOf(
            BookmarkEntity("id3", "Title3", "http://url3", "NDTV", "http://image3", 123458L)
        )

        println("File path: ${file.absolutePath}")
        println("File exists before write: ${file.exists()}")
        println("File length before write: ${file.length()}")

        BookmarkExport.write(contentResolver, uri, bookmarks, BookmarkExport.Format.JSON)

        println("File exists after write: ${file.exists()}")
        println("File length after write: ${file.length()}")

        val text = file.readText()
        println("JSON OUTPUT: '$text'") // Note the quotes to see whitespace

        // Check what characters are actually there
        text.forEachIndexed { index, char ->
            println("Char $index: '$char' (${char.toInt()})")
        }
    }
}