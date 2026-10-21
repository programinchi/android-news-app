package com.example.ch09.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ch09.data.NewsRepository
import com.example.ch09.data.model.Article
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ArticleDetailUiState(
    val isLoading: Boolean = true,
    val article: Article? = null,
    val errorMessage: String? = null
)

class ArticleDetailViewModel(
    private val repository: NewsRepository,
    private val articleUrl: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ArticleDetailUiState()
    )

    val uiState: StateFlow<ArticleDetailUiState> =
        _uiState.asStateFlow()

    init {
        loadArticle()
    }

    private fun loadArticle() {
        viewModelScope.launch {
            try {
                val article = repository.getArticle(articleUrl)

                _uiState.value = if (article != null) {
                    ArticleDetailUiState(
                        isLoading = false,
                        article = article
                    )
                } else {
                    ArticleDetailUiState(
                        isLoading = false,
                        errorMessage = "Artikel tidak ditemukan"
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = ArticleDetailUiState(
                    isLoading = false,
                    errorMessage = "Gagal membuka artikel"
                )
            }
        }
    }
}