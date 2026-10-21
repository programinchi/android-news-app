package com.example.ch09.data

import com.example.ch09.data.mapper.toDomain
import com.example.ch09.data.model.Article
import com.example.ch09.data.model.NewsCategory
import com.example.ch09.data.model.NewsFeed
import com.example.ch09.data.remote.NewsApiService
import com.example.ch09.data.remote.dto.NewsResponseDto
import kotlin.coroutines.cancellation.CancellationException

interface NewsRepository {
    suspend fun getTopHeadlines(
        category: NewsCategory
    ): Result<NewsFeed>

    suspend fun searchNews(
        query: String
    ): Result<NewsFeed>

    suspend fun getArticle(url: String): Article?
}

class NewsRepositoryImpl(
    private val api: NewsApiService
) : NewsRepository {

    private val loadedArticles = mutableMapOf<String, Article>()

    override suspend fun getTopHeadlines(
        category: NewsCategory
    ): Result<NewsFeed> = fetch {
        api.getTopHeadlines(category = category.apiValue)
    }

    override suspend fun searchNews(
        query: String
    ): Result<NewsFeed> = fetch {
        api.searchNews(query = query)
    }

    override suspend fun getArticle(url: String): Article? {
        return loadedArticles[url]
    }

    private suspend fun fetch(
        remote: suspend () -> NewsResponseDto
    ): Result<NewsFeed> {

        val result = runCatching {
            val articles = remote().articles
                .mapNotNull { it.toDomain() }
                .distinctBy { it.url }

            articles.forEach { article ->
                loadedArticles[article.url] = article
            }

            NewsFeed(articles = articles)
        }

        result.exceptionOrNull()?.let {
            if (it is CancellationException) throw it
        }

        return result
    }
}