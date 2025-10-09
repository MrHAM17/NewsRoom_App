package com.example.newsroom.core.di

import android.content.Context
import androidx.room.Room
import com.example.newsroom.BuildConfig
import com.example.newsroom.data.local.room.NewsDatabase
import com.example.newsroom.data.remote.api.NewsApiService
import com.example.newsroom.data.repository.NewsRepository
import com.example.newsroom.core.util.network.hasNetworkSync
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private const val BASE_URL = "https://newsapi.org/"

//    @Provides @Singleton
//    fun provideMoshi(): Moshi = Moshi.Builder().build()

    @Provides @Singleton
    fun provideMoshi(): Moshi =
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()


    // ✅ Cache: 20MB for network responses
    @Provides @Singleton
    fun provideCache(@ApplicationContext ctx: Context): Cache =
        Cache(File(ctx.cacheDir, "http_cache"), 20L * 1024L * 1024L)

    private fun offlineInterceptor(@ApplicationContext ctx: Context) = Interceptor { chain ->
        var request = chain.request()
        if (!hasNetworkSync(ctx)) {
            request = request.newBuilder()
                .header("Cache-Control", "public, max-stale=604800") // 7 days
                .build()

            /*
               If you want strict offline mode (never hit network if offline, even if cache is empty) → keep only-if-cached.
               .header("Cache-Control", "public, only-if-cached, max-stale=604800") // 7 days

               If you want smooth UX (no random 504 Unsatisfiable Request) → remove only-if-cached as shown above.
               .header("Cache-Control", "public, max-stale=604800") // 7 days
            */
        }
        chain.proceed(request)
    }
    private val onlineInterceptor = Interceptor { chain ->
        val resp = chain.proceed(chain.request())
        resp.newBuilder()
            .removeHeader("Pragma")
            .removeHeader("Cache-Control")
            .header("Cache-Control", "public, max-age=120") // 2 mins
            .build()
    }

    @Provides @Singleton
    fun provideOKHttp(@ApplicationContext ctx: Context, cache: Cache): OkHttpClient{
        val logging = HttpLoggingInterceptor().apply {
            level =
                if (BuildConfig.DEBUG) { HttpLoggingInterceptor.Level.BODY }  // full logs for dev
                else { HttpLoggingInterceptor.Level.BASIC } // minimal logs for release
        }

        // Add apiKey automatically
        val apiKeyAdder = Interceptor { chain ->
            val req = chain.request()
            //  val newUrl = req.url.newBuilder().addQueryParameter("apiKey", com.example.newsroom.BuildConfig.NEWS_API_KEY).build()
            val newUrl = req.url.newBuilder().addQueryParameter("apiKey", BuildConfig.NEWS_API_KEY).build()

            // ✅ Log the actual API key at request time
            // Log.d("API_KEY_DEBUG", "API Key = ${BuildConfig.NEWS_API_KEY}")

            chain.proceed(req.newBuilder().url(newUrl).build())
        }

        return OkHttpClient
            .Builder()
            .addInterceptor(apiKeyAdder)
            .addInterceptor(logging)
            .cache(cache) // attach cache
            .addInterceptor(offlineInterceptor(ctx)) // when no internet
            .addNetworkInterceptor(onlineInterceptor) // when online
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    @Provides @Singleton
    fun provideRetrofit(client: OkHttpClient, moshi: Moshi): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides @Singleton
    fun provideApi(retrofit: Retrofit): NewsApiService = retrofit.create(NewsApiService::class.java)

    @Provides @Singleton
    fun provideDb(@ApplicationContext context: Context): NewsDatabase =
        Room.databaseBuilder(context, NewsDatabase::class.java, "news.db")
            .fallbackToDestructiveMigration(false)
            .build()

    @Provides @Singleton
    fun provideRepo(api: NewsApiService, db: NewsDatabase): NewsRepository = NewsRepository(api, db)


}