package com.meetch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.meetch.ui.AppNavHost
import com.meetch.ui.screen.SplashScreen
import com.meetch.ui.theme.MeetchTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MeetchTheme {
                val navController = rememberNavController()
                LaunchedEffect(Unit) {
                    delay(2000)
                    navController.navigate("main_screen") {
                        popUpTo("splash_screen") { inclusive = true }
                    }
                }

                NavHost(navController = navController, startDestination = "splash_screen") {
                    composable("splash_screen") { SplashScreen() }
                    composable("main_screen") { AppNavHost() }
                }
            }
        }
    }
}