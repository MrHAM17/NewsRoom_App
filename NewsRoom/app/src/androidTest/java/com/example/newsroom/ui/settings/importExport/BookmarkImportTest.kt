package com.example.newsroom.ui.settings.importExport

import android.content.ContentResolver
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class BookmarkImportTest {

    private lateinit var contentResolver: ContentResolver

    @Before
    fun setup() {
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        contentResolver = ctx.contentResolver
    }

    @Test
    fun importCsvReadsBookmarks() {
        val file = File.createTempFile("bookmark", ".csv")
        file.writeText("id,title,url,source,image,savedAt\n1,Title1,http://url1,CNN,,123456\n")

        val uri = Uri.fromFile(file)

        val list = BookmarkImport.read(contentResolver, uri)
        assertEquals("Should read 1 bookmark from CSV", 1, list.size)
        assertEquals("Title should match", "Title1", list.first().title)
        assertEquals("URL should match", "http://url1", list.first().url)
    }

    @Test
    fun importJsonReadsBookmarks() {
        val file = File.createTempFile("bookmark", ".json")
        // Write proper JSON format
        file.writeText("""
            [
                {
                    "id": "2",
                    "title": "Title2", 
                    "url": "http://url2",
                    "source": "BBC",
                    "image": null,
                    "savedAt": 123456
                }
            ]
        """.trimIndent())

        val uri = Uri.fromFile(file)

        val list = BookmarkImport.read(contentResolver, uri)
        assertEquals("Should read 1 bookmark from JSON", 1, list.size)
        assertEquals("Title should match", "Title2", list.first().title)
        assertEquals("URL should match", "http://url2", list.first().url)
        assertEquals("Source should match", "BBC", list.first().sourceName)
    }

    @Test
    fun importJsonWithMissingFields() {
        val file = File.createTempFile("bookmark", ".json")
        // JSON with minimal required fields
        file.writeText("""
            [
                {
                    "title": "Minimal Title", 
                    "url": "http://minimal.url"
                }
            ]
        """.trimIndent())

        val uri = Uri.fromFile(file)

        val list = BookmarkImport.read(contentResolver, uri)
        assertEquals("Should read 1 bookmark from minimal JSON", 1, list.size)
        assertEquals("Title should match", "Minimal Title", list.first().title)
        assertEquals("URL should match", "http://minimal.url", list.first().url)
        assertNull("Source should be null when not provided", list.first().sourceName)
    }
}