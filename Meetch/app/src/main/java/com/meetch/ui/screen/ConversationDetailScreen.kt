package com.meetch.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationDetailScreen(navController: NavHostController, conversationTitle: String) {
    val messages = remember { mutableStateListOf("Message 1 dans la conversation", "Message 2 dans la conversation") }
    var newMessage by remember { mutableStateOf("") }
    var isConversationAccepted by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = conversationTitle) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(messages.size) { index ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = messages[index])
                    }
                }
            }

            if (isConversationAccepted) {
                Row(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = newMessage,
                        onValueChange = { newMessage = it },
                        label = { Text("Message") },
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        if (newMessage.isNotEmpty()) {
                            messages.add(newMessage)
                            newMessage = ""
                        }
                    }) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = "Envoyer")
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = {
                        // Supprimer la conversation (retour et suppression)
                        navController.popBackStack()
                    }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Refuser")
                    }
                    IconButton(onClick = {
                        // Accepter la conversation et permettre l'envoi de messages
                        isConversationAccepted = true
                    }) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Accepter")
                    }
                }
            }
        }
    }
}