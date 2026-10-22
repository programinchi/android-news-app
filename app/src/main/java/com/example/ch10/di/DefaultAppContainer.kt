package com.example.ch10.di

import android.content.Context
import com.example.ch10.BuildConfig
import com.example.ch10.data.NewsRepository
import com.example.ch10.data.NewsRepositoryImpl
import com.example.ch10.data.local.NewsDatabase
import com.example.ch10.data.remote.NEWS_BASE_URL
import com.example.ch10.data.remote.createNewsApiService
import com.example.ch10.data.remote.createOkHttpClient
import com.example.ch10.util.Clock

// Peta refactor: setiap bagian di bawah ini pindah ke sebuah module Hilt
// di package di/. Setelah semuanya pindah, hapus file ini (T1.8).
class DefaultAppContainer(context: Context) : AppContainer {

    // TODO [T2.1] Pindahkan ke di/DatabaseModule.kt:
    //  @Provides @Singleton NewsDatabase (Room.databaseBuilder + @ApplicationContext)
    //  dan @Provides ArticleDao. Setelah itu hapus NewsDatabase.getInstance().
    private val database: NewsDatabase by lazy {
        NewsDatabase.getInstance(context)
    }

    // TODO [T1.3] Pindahkan ke di/NetworkModule.kt (object, @InstallIn(SingletonComponent::class)):
    //  @Provides @Singleton untuk Json, OkHttpClient, dan NewsApiService.
    //  Fungsi pembuatnya sudah ada di data/remote/NetworkFactory.kt.
    private val newsApiService by lazy {
        createNewsApiService(
            baseUrl = NEWS_BASE_URL,
            client = createOkHttpClient(
                apiKey = BuildConfig.NEWS_API_KEY,
                enableLogging = BuildConfig.DEBUG
            )
        )
    }

    // TODO [T2.2] Ganti dengan di/RepositoryModule.kt (abstract class):
    //  @Binds @Singleton NewsRepositoryImpl -> NewsRepository.
    // TODO [T1.4] Clock disediakan lewat di/AppModule.kt (@Provides).
    override val newsRepository: NewsRepository by lazy {
        NewsRepositoryImpl(
            api = newsApiService,
            dao = database.articleDao(),
            clock = Clock { System.currentTimeMillis() }
        )
    }
}
