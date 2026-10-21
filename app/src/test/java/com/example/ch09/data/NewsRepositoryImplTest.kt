package com.example.ch09.data

import com.example.ch09.data.model.NewsCategory
import com.example.ch09.data.remote.dto.ArticleDto
import com.example.ch09.testutil.FakeArticleDao
import com.example.ch09.testutil.FakeNewsApiService
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class NewsRepositoryImplTest {

    private fun sampleArticle() = ArticleDto(
        title = "Berita Teknologi Terbaru",
        author = "Redaksi",
        description = "Perkembangan teknologi dan AI",
        url = "https://contoh.com/berita-teknologi",
        publishedAt = "2026-10-21T08:00:00Z"
    )

    @Test
    fun `successful request saves articles to cache`() = runTest {

        val api = FakeNewsApiService().apply {
            articles = listOf(sampleArticle())
        }

        val dao = FakeArticleDao()

        val repository = NewsRepositoryImpl(
            api = api,
            dao = dao,
            clock = { 1000L }
        )

        val result = repository.getTopHeadlines(
            NewsCategory.Technology
        )

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrThrow().articles.size)
        assertNull(result.getOrThrow().offline)

        val cached = dao.getArticles("technology")

        assertEquals(1, cached.size)
        assertEquals(1000L, cached.first().cachedAt)

        val detail = repository.getArticle(
            "https://contoh.com/berita-teknologi"
        )

        assertNotNull(detail)
        assertEquals(
            "Berita Teknologi Terbaru",
            detail?.title
        )
    }

    @Test
    fun `failed request loads offline cache`() = runTest {

        val api = FakeNewsApiService().apply {
            articles = listOf(sampleArticle())
        }

        val dao = FakeArticleDao()

        val repository = NewsRepositoryImpl(
            api = api,
            dao = dao,
            clock = { 2000L }
        )

        repository.getTopHeadlines(
            NewsCategory.Technology
        )

        api.error = IOException("Offline")

        val result = repository.getTopHeadlines(
            NewsCategory.Technology
        )

        assertTrue(result.isSuccess)

        val feed = result.getOrThrow()

        assertEquals(1, feed.articles.size)
        assertNotNull(feed.offline)
        assertEquals(2000L, feed.offline?.cachedAt)
        assertEquals(
            "Tidak ada koneksi internet.",
            feed.offline?.reason
        )
    }

    @Test
    fun `failed request without cache returns error`() = runTest {

        val api = FakeNewsApiService().apply {
            error = IOException("Offline")
        }

        val repository = NewsRepositoryImpl(
            api = api,
            dao = FakeArticleDao()
        )

        val result = repository.getTopHeadlines(
            NewsCategory.General
        )

        assertTrue(result.isFailure)
        assertFalse(result.isSuccess)
    }

    @Test
    fun `offline search finds cached articles`() = runTest {

        val api = FakeNewsApiService().apply {
            articles = listOf(sampleArticle())
        }

        val repository = NewsRepositoryImpl(
            api = api,
            dao = FakeArticleDao()
        )

        repository.getTopHeadlines(
            NewsCategory.Technology
        )

        api.error = IOException("Offline")

        val result = repository.searchNews("Teknologi")

        assertTrue(result.isSuccess)

        val feed = result.getOrThrow()

        assertEquals(1, feed.articles.size)
        assertNotNull(feed.offline)
        assertEquals(
            "Berita Teknologi Terbaru",
            feed.articles.first().title
        )
    }
}