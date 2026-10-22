package com.example.ch10.ui.news

import com.example.ch10.data.NewsRepository
import com.example.ch10.data.model.NewsCategory
import com.example.ch10.data.model.NewsFeed
import com.example.ch10.testutil.MainDispatcherRule
import com.example.ch10.testutil.article
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class NewsViewModelMockKTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    private lateinit var repository: NewsRepository

    @Before
    fun setUp() {
        repository = mockk()
    }

    @Test
    fun `sukses - headline mengisi daftar artikel`() = runTest {

        val fakeFeed = NewsFeed(
            articles = listOf(
                article(1),
                article(2)
            )
        )

        coEvery {
            repository.getTopHeadlines(NewsCategory.General)
        } returns Result.success(fakeFeed)

        val viewModel = NewsViewModel(repository)

        runCurrent()

        val state = viewModel.uiState.value

        assertFalse(state.isLoading)

        assertEquals(
            2,
            state.articles.size
        )

        assertNull(state.errorMessage)

        assertEquals(
            "Berita 1",
            state.articles[0].title
        )

        assertEquals(
            "Berita 2",
            state.articles[1].title
        )

        coVerify(exactly = 1) {
            repository.getTopHeadlines(
                NewsCategory.General
            )
        }
    }

    @Test
    fun `gagal - pesan error tampil dan daftar kosong`() = runTest {

        coEvery {
            repository.getTopHeadlines(NewsCategory.General)
        } returns Result.failure(
            IOException("Network unavailable")
        )

        val viewModel = NewsViewModel(repository)

        runCurrent()

        val state = viewModel.uiState.value

        assertFalse(state.isLoading)

        assertTrue(state.articles.isEmpty())

        assertEquals(
            "Tidak ada koneksi internet.",
            state.errorMessage
        )

        coVerify(exactly = 1) {
            repository.getTopHeadlines(
                NewsCategory.General
            )
        }
    }
}