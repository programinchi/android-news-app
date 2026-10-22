package com.example.ch10.di

import com.example.ch10.data.NewsRepository

// Manual DI (Chapter 6 dan 9): satu tempat yang tahu cara merakit dependency.
// TODO [T1.8] Hapus file ini setelah semua dependency disediakan Hilt.
interface AppContainer {
    val newsRepository: NewsRepository
}
