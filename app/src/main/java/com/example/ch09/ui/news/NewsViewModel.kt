package com.example.ch09.ui.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ch09.data.NewsRepository
import com.example.ch09.data.model.Article
import com.example.ch09.data.model.NewsCategory
import com.example.ch09.data.toUserMessage
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NewsUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val articles: List<Article> = emptyList(),
    val categories: List<NewsCategory> = NewsCategory.entries,
    val selectedCategory: NewsCategory = NewsCategory.General,
    val query: String = "",
    val errorMessage: String? = null
) {
    val isSearching: Boolean
        get() = query.isNotBlank()
}

class NewsViewModel(
    private val repository: NewsRepository,
    private val searchDebounceMs: Long = 500L
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        NewsUiState(isLoading = true)
    )

    val uiState: StateFlow<NewsUiState> =
        _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        load()
    }

    fun onCategorySelected(category: NewsCategory) {
        if (category == _uiState.value.selectedCategory) return

        _uiState.update {
            it.copy(selectedCategory = category)
        }

        load()
    }

    fun onQueryChange(query: String) {
        _uiState.update {
            it.copy(query = query)
        }

        val debounce = if (query.isBlank()) {
            0L
        } else {
            searchDebounceMs
        }

        load(delayMs = debounce)
    }

    fun refresh() {
        load(isRefresh = true)
    }

    fun retry() {
        load()
    }

    private fun load(
        delayMs: Long = 0L,
        isRefresh: Boolean = false
    ) {
        loadJob?.cancel()

        loadJob = viewModelScope.launch {

            if (delayMs > 0L) {
                delay(delayMs)
            }

            _uiState.update {
                it.copy(
                    isLoading = !isRefresh,
                    isRefreshing = isRefresh,
                    errorMessage = null
                )
            }

            val state = _uiState.value
            val query = state.query.trim()

            val result = if (query.isEmpty()) {
                repository.getTopHeadlines(
                    state.selectedCategory
                )
            } else {
                repository.searchNews(query)
            }

            result
                .onSuccess { feed ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            articles = feed.articles,
                            errorMessage = null
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            articles = emptyList(),
                            errorMessage = error.toUserMessage()
                        )
                    }
                }
        }
    }
}