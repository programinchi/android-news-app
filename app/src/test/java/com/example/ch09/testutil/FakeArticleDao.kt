package com.example.ch09.testutil

import com.example.ch09.data.local.ArticleDao
import com.example.ch09.data.local.ArticleEntity

class FakeArticleDao : ArticleDao() {

    private val cache = mutableListOf<ArticleEntity>()

    override suspend fun getArticles(
        category: String
    ): List<ArticleEntity> {

        return cache
            .filter { it.category == category }
            .sortedBy { it.position }
    }

    override suspend fun getArticleByUrl(
        url: String
    ): ArticleEntity? {

        return cache
            .filter { it.url == url }
            .maxByOrNull { it.cachedAt }
    }

    override suspend fun searchCached(
        query: String
    ): List<ArticleEntity> {

        return cache
            .filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true)
            }
            .sortedByDescending { it.cachedAt }
            .distinctBy { it.url }
    }

    override suspend fun insertAll(
        articles: List<ArticleEntity>
    ) {

        cache.removeAll { stored ->
            articles.any {
                it.category == stored.category &&
                        it.url == stored.url
            }
        }

        cache.addAll(articles)
    }

    override suspend fun deleteCategory(
        category: String
    ) {
        cache.removeAll { it.category == category }
    }
}