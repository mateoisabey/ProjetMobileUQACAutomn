package com.meetch.ui

import androidx.compose.foundation.layout.*
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

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.ui.Modifier
import com.meetch.ui.screen.ConversationDetailScreen
import com.meetch.ui.screen.ConversationsScreen
import com.meetch.ui.screen.PostScreen
import com.meetch.ui.screen.ProfileScreen
import com.meetch.ui.screen.SwipeScreen

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

@Composable
fun AppNavHost() {
    val navController: NavHostController = rememberNavController()
    var selectedItem by remember { mutableStateOf(0) }
    val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController, selectedItem, onItemSelected = { selectedItem = it })
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "swipe",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("swipe") {
                SwipeScreen()
            }
            composable("conversations") {
                ConversationsScreen(navController)
            }
            composable("conversationDetail/{conversationTitle}") { backStackEntry ->
                val conversationTitle = backStackEntry.arguments?.getString("conversationTitle") ?: "Détails"
                ConversationDetailScreen(navController, conversationTitle)
            }
            composable("post") {
                PostScreen { activityData ->
                    println("Activité créée : $activityData")
                    navController.popBackStack()
                }
            }
            composable("profile") {
                ProfileScreen(
                    onEditProfile = { }
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