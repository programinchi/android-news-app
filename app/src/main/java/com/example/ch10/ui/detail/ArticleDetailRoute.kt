package com.example.ch10.ui.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.ch10.MainApplication
import com.example.ch10.ui.openUrl

@Composable
fun ArticleDetailRoute(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    // TODO [T1.7] Ganti seluruh factory ini dengan hiltViewModel().
    //  createSavedStateHandle() tidak diperlukan lagi: Hilt menyediakannya.
    val app = context.applicationContext as MainApplication
    val viewModel: ArticleDetailViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                ArticleDetailViewModel(app.container.newsRepository, createSavedStateHandle())
            }
        }
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ArticleDetailScreen(
        uiState = uiState,
        onReadMore = { url -> openUrl(context, url) },
        onNavigateBack = onNavigateBack
    )
}
