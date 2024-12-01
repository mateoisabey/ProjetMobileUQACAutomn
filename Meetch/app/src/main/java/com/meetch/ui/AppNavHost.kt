package com.meetch.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.Modifier
import com.meetch.auth.FirebaseAuthManager
import com.meetch.ui.screen.ConversationDetailScreen
import com.meetch.ui.screen.ConversationsScreen
import com.meetch.ui.screen.LoginScreen
import com.meetch.ui.screen.ProfileScreen
import com.meetch.ui.screen.SwipeScreen
import com.meetch.ui.screen.PostScreen

@Composable
fun AppNavHost(authManager: FirebaseAuthManager) {
    val navController: NavHostController = rememberNavController()
    var selectedItem by remember { mutableStateOf(0) }
    val startDestination = if (authManager.isUserLoggedIn()) "swipe" else "login"

    Scaffold(
        bottomBar = {
            if (startDestination != "login") { // Cacher la barre de navigation sur l'écran de connexion
                BottomNavigationBar(navController, selectedItem, onItemSelected = { selectedItem = it })
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") {
                LoginScreen(
                    authManager = authManager,
                    navController = navController,
                    onLoginSuccess = {
                        navController.navigate("swipe") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }
            composable("swipe") {
                SwipeScreen()
            }
            composable("conversations") {
                ConversationsScreen(navController)
            }
            composable("conversationDetail/{conversationTitle}/{messageRequestId}/{fromUserId}") { backStackEntry ->
                val conversationTitle = backStackEntry.arguments?.getString("conversationTitle") ?: "Détails"
                val messageRequestId = backStackEntry.arguments?.getString("messageRequestId") ?: ""
                val fromUserId = backStackEntry.arguments?.getString("fromUserId") ?: ""
                ConversationDetailScreen(navController, conversationTitle, messageRequestId, fromUserId)
            }
            composable("post") {
                PostScreen(
                    onActivityCreated = { activityData ->
                        println("Activité créée : $activityData")
                        navController.popBackStack()
                    },
                    onCancel = {
                        navController.popBackStack()
                    }
                )
            }
            composable("profile") {
                ProfileScreen(
                    onEditProfile = { },
                    onLogout = {
                        authManager.logout()
                        navController.navigate("login") {
                            popUpTo("swipe") { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    selectedItem: Int,
    onItemSelected: (Int) -> Unit
) {
    val items = listOf(
        BottomNavItem("swipe", "Activités", Icons.Default.Home),
        BottomNavItem("conversations", "Messages", Icons.Default.MailOutline),
        BottomNavItem("post", "Ajout", Icons.Default.Add),
        BottomNavItem("profile", "Profil", Icons.Default.Person)
    )

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedItem == index,
                onClick = {
                    onItemSelected(index)
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                icon = {
                    Icon(item.icon, contentDescription = item.label)
                },
                label = { Text(item.label) },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors()
            )
        }
    }
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)