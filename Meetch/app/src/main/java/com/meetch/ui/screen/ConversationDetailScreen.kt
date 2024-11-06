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
    val messages = remember { mutableStateListOf<Pair<String, String>>() } // Pair de message et de l'auteur (fromUserId)
    var newMessage by remember { mutableStateOf("") }
    var isConversationAccepted by remember { mutableStateOf(false) }
    val isRequestReceiver = currentUser?.uid != fromUserId // Est-ce que l'utilisateur connecté est le destinataire de la requête ?

    // Charger les messages de la conversation existante
    LaunchedEffect(Unit) {
        db.collection("conversations").document(messageRequestId).collection("messages")
            .get()
            .addOnSuccessListener { result ->
                messages.clear()
                for (document in result) {
                    val message = document.getString("message") ?: ""
                    val senderId = document.getString("fromUserId") ?: ""
                    messages.add(senderId to message)
                }
            }
            .addOnFailureListener { exception ->
                println("Erreur lors de la récupération des messages : ${exception.message}")
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
                modifier = Modifier.weight(1f)
            ) {
                items(messages.size) { index ->
                    val (senderId, messageText) = messages[index]
                    val isCurrentUser = senderId == currentUser?.uid
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isCurrentUser) "Moi: $messageText" else "$conversationTitle: $messageText",
                            style = MaterialTheme.typography.bodyMedium
                        )
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
                                    messages.add((currentUser?.uid to newMessage) as Pair<String, String>)
                                    newMessage = ""
                                }
                                .addOnFailureListener { exception ->
                                    println("Erreur lors de l'envoi du message : ${exception.message}")
                                }
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
                                navController.popBackStack()
                            }
                            .addOnFailureListener { exception ->
                                println("Erreur lors de la suppression de la demande de message : ${exception.message}")
                            }
                    }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Refuser")
                    }
                    IconButton(onClick = {
                        db.collection("conversations").document(messageRequestId)
                            .set(mapOf("fromUserId" to fromUserId, "toUserId" to currentUser?.uid))
                            .addOnSuccessListener {
                                isConversationAccepted = true
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