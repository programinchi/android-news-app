package com.example.ch10

import android.app.Application
import com.example.ch10.di.AppContainer
import com.example.ch10.di.DefaultAppContainer

// TODO [T1.1] Tandai class ini dengan @HiltAndroidApp, lalu hapus properti
//  `container` dan isi onCreate(). Hilt yang akan merakit dependency.
class MainApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
