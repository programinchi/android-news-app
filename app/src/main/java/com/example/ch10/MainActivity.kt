package com.example.ch10

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.ch10.ui.theme.Ch10Theme

// TODO [T1.2] Tandai dengan @AndroidEntryPoint. Tanpa anotasi ini,
//  hiltViewModel() di dalam Activity akan crash saat dijalankan.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Ch10Theme { KabarKiniApp() }
        }
    }
}
