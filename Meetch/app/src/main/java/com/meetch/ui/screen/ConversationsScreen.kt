package com.meetch.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun ConversationsScreen(navController: NavHostController) {
    val conversations = listOf("Tennis avec Alex", "Basket avec Chris", "Yoga avec Sam")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Mes Conversations", style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(conversations.size) { index ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            // Navigue vers l'écran de détails de conversation
                            navController.navigate("conversationDetail/${conversations[index]}")
                        }
                ) {
                    Text(
                        text = conversations[index],
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}