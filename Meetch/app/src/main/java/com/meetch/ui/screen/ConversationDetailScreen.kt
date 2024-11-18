package com.meetch.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationDetailScreen(
    navController: NavHostController,
    conversationTitle: String,
    messageRequestId: String,
    fromUserId: String
) {
    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val messages = remember { mutableStateListOf<Triple<String, String, Long>>() } // Triple de senderId, message, timestamp
    var newMessage by remember { mutableStateOf("") }
    var isConversationAccepted by remember { mutableStateOf(false) }
    val isRequestReceiver = currentUser?.uid != fromUserId

    // Charger les messages en temps réel
    LaunchedEffect(Unit) {
        db.collection("conversations").document(messageRequestId).collection("messages")
            .orderBy("timestamp") // Assurez-vous que Firestore est bien configuré pour cette règle d'ordre
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    println("Erreur lors de la récupération des messages : ${e.message}")
                    return@addSnapshotListener
                }

                val updatedMessages = snapshot?.documents?.map { document ->
                    val message = document.getString("message") ?: ""
                    val senderId = document.getString("fromUserId") ?: ""
                    val timestamp = document.getLong("timestamp") ?: 0L
                    Triple(senderId, message, timestamp)
                } ?: emptyList()

                messages.clear()
                messages.addAll(updatedMessages)
            }

        currentUser?.let { user ->
            db.collection("conversations").document(messageRequestId)
                .update("lastReadTimestamp_${user.uid}", System.currentTimeMillis())
        }

        // Vérifier si la conversation a déjà été acceptée
        db.collection("conversations").document(messageRequestId)
            .get()
            .addOnSuccessListener { document ->
                isConversationAccepted = document.exists()
            }
            .addOnFailureListener { exception ->
                println("Erreur lors de la vérification de l'acceptation de la conversation : ${exception.message}")
            }
    }

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
                modifier = Modifier.weight(1f),
                reverseLayout = false // Assurez-vous que cette propriété est `false`
            ) {
                items(messages) { (senderId, messageText, _) ->
                    val isCurrentUser = senderId == currentUser?.uid
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCurrentUser) Color(0xFF4CAF50) else Color(0xFF2196F3)
                            ),
                            modifier = Modifier
                                .widthIn(max = 250.dp)
                        ) {
                            Text(
                                text = messageText,
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }

            if (isConversationAccepted) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newMessage,
                        onValueChange = { newMessage = it },
                        label = { Text("Message") },
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        if (newMessage.isNotEmpty()) {
                            val messageData = mapOf(
                                "fromUserId" to currentUser?.uid,
                                "toUserId" to fromUserId,
                                "message" to newMessage,
                                "timestamp" to System.currentTimeMillis()
                            )
                            db.collection("conversations").document(messageRequestId)
                                .collection("messages")
                                .add(messageData)
                                .addOnSuccessListener {
                                    newMessage = ""
                                }
                                .addOnFailureListener { exception ->
                                    println("Erreur lors de l'envoi du message : ${exception.message}")
                                }

                            db.collection("conversations").document(messageRequestId)
                                .update("lastMessageTimestamp", System.currentTimeMillis())
                        }
                    }) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = "Envoyer")
                    }
                }
            } else if (isRequestReceiver) {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = {
                        db.collection("messageRequests").document(messageRequestId)
                            .delete()
                            .addOnSuccessListener {
                                println("Requête refusée et supprimée.")
                                navController.popBackStack()
                            }
                            .addOnFailureListener { exception ->
                                println("Erreur lors de la suppression de la requête : ${exception.message}")
                            }
                    }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Refuser")
                    }
                    IconButton(onClick = {
                        db.collection("conversations").document(messageRequestId)
                            .set(
                                mapOf(
                                    "fromUserId" to fromUserId,
                                    "toUserId" to currentUser?.uid,
                                    "participants" to listOf(fromUserId, currentUser?.uid),
                                    "lastMessageTimestamp" to System.currentTimeMillis()
                                )
                            )
                            .addOnSuccessListener {
                                db.collection("messageRequests").document(messageRequestId).delete()
                                    .addOnSuccessListener {
                                        println("Requête acceptée et convertie en conversation.")
                                        isConversationAccepted = true
                                    }
                                    .addOnFailureListener { exception ->
                                        println("Erreur lors de la suppression de la requête : ${exception.message}")
                                    }
                            }
                            .addOnFailureListener { exception ->
                                println("Erreur lors de la création de la conversation : ${exception.message}")
                            }
                    }) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Accepter")
                    }
                }
            }
        }
    }
}