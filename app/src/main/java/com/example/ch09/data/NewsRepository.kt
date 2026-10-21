package com.example.ch09.data

import com.example.ch09.data.local.ArticleDao
import com.example.ch09.data.local.ArticleEntity
import com.example.ch09.data.mapper.toDomain
import com.example.ch09.data.mapper.toEntity
import com.example.ch09.data.model.Article
import com.example.ch09.data.model.NewsCategory
import com.example.ch09.data.model.NewsFeed
import com.example.ch09.data.model.OfflineInfo
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
    private val api: NewsApiService,
    private val dao: ArticleDao,
    private val clock: () -> Long = System::currentTimeMillis
) : NewsRepository {

    companion object {
        private const val SEARCH_CACHE_KEY = "__search__"
    }

    override suspend fun getTopHeadlines(
        category: NewsCategory
    ): Result<NewsFeed> {

        val cacheKey = category.apiValue

        return fetch(
            cacheKey = cacheKey,
            remote = {
                api.getTopHeadlines(
                    category = category.apiValue
                )
            },
            readCached = {
                dao.getArticles(cacheKey)
            }
        )
    }

    override suspend fun searchNews(
        query: String
    ): Result<NewsFeed> {

        return fetch(
            cacheKey = SEARCH_CACHE_KEY,
            remote = {
                api.searchNews(query = query)
            },
            readCached = {
                dao.searchCached(query)
            }
        )
    }

    override suspend fun getArticle(
        url: String
    ): Article? {
        return dao.getArticleByUrl(url)?.toDomain()
    }

    private suspend fun fetch(
        cacheKey: String,
        remote: suspend () -> NewsResponseDto,
        readCached: suspend () -> List<ArticleEntity>
    ): Result<NewsFeed> {

        return try {

            val articles = remote()
                .articles
                .mapNotNull { it.toDomain() }
                .distinctBy { it.url }

            val cachedAt = clock()

            val entities = articles.mapIndexed { index, article ->
                article.toEntity(
                    category = cacheKey,
                    position = index,
                    cachedAt = cachedAt
                )
            }

            dao.replaceCategory(
                category = cacheKey,
                articles = entities
            )

            Result.success(
                NewsFeed(
                    articles = articles,
                    offline = null
                )
            )

        } catch (e: CancellationException) {

            throw e

        } catch (e: Exception) {

            try {
                val cachedArticles = readCached()

                if (cachedArticles.isEmpty()) {
                    Result.failure(e)
                } else {

                    val articles = cachedArticles
                        .map { it.toDomain() }
                        .distinctBy { it.url }

                    val lastUpdated = cachedArticles.maxOf {
                        it.cachedAt
                    }

                    Result.success(
                        NewsFeed(
                            articles = articles,
                            offline = OfflineInfo(
                                cachedAt = lastUpdated,
                                reason = e.toUserMessage()
                            )
                        )
                    )
                }

            } catch (cancel: CancellationException) {
                throw cancel
            } catch (_: Exception) {
                Result.failure(e)
            }
        }
    }
}