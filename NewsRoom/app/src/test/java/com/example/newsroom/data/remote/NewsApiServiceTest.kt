package com.example.newsroom.data.remote


import com.example.newsroom.data.remote.api.NewsApiService
import com.example.newsroom.data.remote.model.EverythingResponse
import com.example.newsroom.data.remote.model.SourcesResponse
import com.example.newsroom.data.remote.model.TopHeadlinesResponse
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class NewsApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: NewsApiService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        api = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsApiService::class.java)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    // -----------------------------
    // getTopHeadlines
    // -----------------------------
    @Test
    fun getTopHeadlines_returnsExpectedResponse() = runBlocking {
        val mockJson = """
            {
              "status": "ok",
              "totalResults": 1,
              "articles": [
                {
                  "source": {"id": "cnn", "name": "CNN"},
                  "author": "John Doe",
                  "title": "Test News Title",
                  "description": "Test description",
                  "url": "https://example.com/test",
                  "urlToImage": "https://example.com/image.jpg",
                  "publishedAt": "2023-01-01T00:00:00Z",
                  "content": "Test content"
                }
              ]
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(mockJson))

        val response: TopHeadlinesResponse = api.getTopHeadlines(country = "us")

        assertThat(response.status).isEqualTo("ok")
        assertThat(response.totalResults).isEqualTo(1)
        assertThat(response.articles?.get(0)?.title).isEqualTo("Test News Title")
    }

    @Test
    fun getTopHeadlines_handlesErrorResponse() = runBlocking {
        mockWebServer.enqueue(MockResponse().setResponseCode(500))

        try {
            api.getTopHeadlines(country = "us")
            assert(false) { "Expected exception was not thrown" }
        } catch (e: Exception) {
            assertThat(e).isInstanceOf(Exception::class.java)
        }
    }

    // -----------------------------
    // getEverything
    // -----------------------------
    @Test
    fun getEverything_returnsExpectedResponse() = runBlocking {
        val mockJson = """
            {
              "status": "ok",
              "totalResults": 1,
              "articles": [
                {
                  "source": {"id": "bbc", "name": "BBC"},
                  "author": "Alice",
                  "title": "Everything Test Title",
                  "description": "Everything description",
                  "url": "https://example.com/everything",
                  "urlToImage": null,
                  "publishedAt": "2023-02-01T00:00:00Z",
                  "content": "Everything content"
                }
              ]
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(mockJson))

        val response: EverythingResponse = api.getEverything(query = "india")

        assertThat(response.status).isEqualTo("ok")
        assertThat(response.totalResults).isEqualTo(1)
        assertThat(response.articles?.get(0)?.title).isEqualTo("Everything Test Title")
    }

    @Test
    fun getEverything_handlesErrorResponse() = runBlocking {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))

        try {
            api.getEverything(query = "india")
            assert(false) { "Expected exception was not thrown" }
        } catch (e: Exception) {
            assertThat(e).isInstanceOf(Exception::class.java)
        }
    }

    // -----------------------------
    // getSources
    // -----------------------------
    @Test
    fun getSources_returnsExpectedResponse() = runBlocking {
        val mockJson = """
            {
              "status": "ok",
              "sources": [
                {
                  "id": "abc-news",
                  "name": "ABC News",
                  "description": "Your trusted source for breaking news.",
                  "url": "https://abcnews.go.com",
                  "category": "general",
                  "language": "en",
                  "country": "us"
                }
              ]
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(mockJson))

        val response: SourcesResponse = api.getSources(category = "general")

        assertThat(response.status).isEqualTo("ok")
        assertThat(response.sources).hasSize(1)
        assertThat(response.sources[0].id).isEqualTo("abc-news")
    }

    @Test
    fun getSources_handlesErrorResponse() = runBlocking {
        mockWebServer.enqueue(MockResponse().setResponseCode(400))

        try {
            api.getSources(category = "general")
            assert(false) { "Expected exception was not thrown" }
        } catch (e: Exception) {
            assertThat(e).isInstanceOf(Exception::class.java)
        }
    }
}
