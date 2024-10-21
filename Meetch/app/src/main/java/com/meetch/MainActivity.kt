package com.meetch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.meetch.ui.AppNavHost
import com.meetch.ui.theme.MeetchTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MeetchTheme {
                AppNavHost()
            }
        }
    }
}