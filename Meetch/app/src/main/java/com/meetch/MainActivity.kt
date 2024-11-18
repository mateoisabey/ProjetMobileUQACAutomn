package com.meetch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.meetch.auth.FirebaseAuthManager
import com.meetch.ui.AppNavHost
import com.meetch.ui.screen.LoginScreen
import com.meetch.ui.screen.SignUpScreen

class MainActivity : ComponentActivity() {

    private val authManager = FirebaseAuthManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "login") {
                composable("login") {
                    LoginScreen(authManager, navController) {
                        navController.navigate("main_screen")
                    }
                }
                composable("sign_up") {
                    SignUpScreen(authManager) {
                        navController.navigate("main_screen")
                    }
                }
                composable("main_screen") {
                    AppNavHost(authManager = authManager)
                }
            }
        }
    }
}
