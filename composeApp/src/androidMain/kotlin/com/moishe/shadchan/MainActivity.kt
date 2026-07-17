package com.moishe.shadchan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import com.moishe.shadchan.platform.PlatformContext
import com.moishe.shadchan.ui.ShadchanViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel = remember { ShadchanViewModel(PlatformContext(applicationContext)) }
            App(viewModel)
        }
    }
}
