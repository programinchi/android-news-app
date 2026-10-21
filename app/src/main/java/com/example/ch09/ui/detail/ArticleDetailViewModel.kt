package com.example.ch09.ui.detail

import androidx.lifecycle.ViewModel
import com.example.ch09.data.NewsRepository
import com.example.ch09.data.model.Article
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ArticleDetailUiState(
    val isLoading: Boolean = true,
    val article: Article? = null,
    val errorMessage: String? = null
)

// Tugas 2: layar detail. Yang dikirim lewat navigasi hanya URL artikel;
// datanya diambil dari repository, bukan objek Article utuh.
class ArticleDetailViewModel(
    private val repository: NewsRepository,
    private val articleUrl: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(ArticleDetailUiState())
    val uiState: StateFlow<ArticleDetailUiState> = _uiState.asStateFlow()

    init {
        // TODO [T2.3] Di viewModelScope.launch, panggil repository.getArticle(articleUrl):
        //   - ditemukan -> isLoading = false, article = artikel
        //   - null      -> isLoading = false, errorMessage = "Artikel tidak ditemukan"
        //   Tes yang sudah disediakan: ArticleDetailViewModelTest (merah sampai ini selesai).
    }
}

// TODO [T2.4] Buat ArticleDetailRoute.kt (ViewModel + openUrl) dan
//   ArticleDetailScreen.kt (gambar, sumber · waktu, judul, "Oleh <penulis>",
//   deskripsi, potongan isi, tombol "Baca Selengkapnya") di folder ini.
//   Pola Route/Screen sama dengan ui/news/.
