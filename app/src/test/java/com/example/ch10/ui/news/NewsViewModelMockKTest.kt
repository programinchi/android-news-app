package com.example.ch10.ui.news

import com.example.ch10.data.NewsRepository
import com.example.ch10.testutil.MainDispatcherRule
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test

// Tugas 3: uji NewsViewModel dengan MockK (bukan FakeNewsRepository).
//  - coEvery { repository.getTopHeadlines(any()) } returns Result.success(...)
//  - buat ViewModel: NewsViewModel(repository) -> init langsung memuat headline
//  - runCurrent() agar coroutine di viewModelScope selesai
//  - periksa viewModel.uiState.value, dan coVerify { ... } untuk jumlah panggilan
// Helper article(id) ada di testutil/TestData.kt. Hilt TIDAK diperlukan di sini.
@OptIn(ExperimentalCoroutinesApi::class)
class NewsViewModelMockKTest {

    @get:Rule val mainRule = MainDispatcherRule()

    private lateinit var repository: NewsRepository

    @Before fun setUp() {
        repository = mockk()
    }

    // TODO [T3.1] Skenario sukses: repository mengembalikan 2 artikel ->
    //  isLoading false, articles.size == 2, errorMessage null,
    //  dan getTopHeadlines(NewsCategory.General) dipanggil tepat 1 kali.
    @Test fun `sukses - headline mengisi daftar artikel`() = runTest {
        fail("Belum dikerjakan: T3.1")
    }

    // TODO [T3.2] Skenario gagal: repository mengembalikan Result.failure(IOException()) ->
    //  articles kosong dan errorMessage == "Tidak ada koneksi internet."
    @Test fun `gagal - pesan error tampil dan daftar kosong`() = runTest {
        fail("Belum dikerjakan: T3.2")
    }
}
