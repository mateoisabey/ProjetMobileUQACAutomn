package com.meetch.ui.screen

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextDecoration
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
    val messages = remember { mutableStateListOf<Triple<String, String, Long>>() }
    var newMessage by remember { mutableStateOf("") }
    var isConversationAccepted by remember { mutableStateOf(false) }
    val isRequestReceiver = currentUser?.uid != fromUserId

    var isProfileDialogOpen by remember { mutableStateOf(false) }
    var userInfo by remember { mutableStateOf(mapOf<String, String>()) }

    /**
     * Chargement des messages en temps réel depuis Firestore.
     */
    LaunchedEffect(Unit) {
        db.collection("conversations").document(messageRequestId).collection("messages")
            .orderBy("timestamp")
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

        // Marquer les messages comme lus pour l'utilisateur actuel.
        currentUser?.let { user ->
            db.collection("conversations").document(messageRequestId)
                .update("lastReadTimestamp_${user.uid}", System.currentTimeMillis())
        }

        // Vérifier si la conversation a été acceptée.
        db.collection("conversations").document(messageRequestId)
            .get()
            .addOnSuccessListener { document ->
                isConversationAccepted = document.exists()
            }
            .addOnFailureListener { exception ->
                println("Erreur lors de la vérification de l'acceptation de la conversation : ${exception.message}")
            }
    }
    /**
     * Récupère les données de l'utilisateur spécifié.
     */
    fun fetchUserInfo(userId: String) {
        db.collection("userData").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    userInfo = mapOf(
                        "name" to (document.getString("name") ?: "Inconnu"),
                        "age" to (document.getString("age") ?: "Inconnu"),
                        "gender" to (document.getString("gender") ?: "Inconnu"),
                        "city" to (document.getString("city") ?: "Inconnu"),
                        "sports" to (document.get("sports") as? List<*> ?: listOf<String>()).joinToString(", ")
                    )
                    isProfileDialogOpen = true
                }
            }
            .addOnFailureListener { exception ->
                println("Erreur lors de la récupération des informations de l'utilisateur : ${exception.message}")
            }
    }


    /**
     * Charge les informations de profil d'un utilisateur à partir de Firestore.
     */
    fun loadUserInfo() {
       if (currentUser?.uid == fromUserId) {
            // Si l'utilisateur actuel est le `fromUserId`, alors charger le `toUserId`.
            db.collection("conversations").document(messageRequestId)
                .get()
                .addOnSuccessListener { document ->
                    val toUserId = document.getString("toUserId") ?: ""
                    fetchUserInfo(toUserId)
                }
        } else {
            fetchUserInfo(fromUserId)
        }
    }



    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = conversationTitle,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { loadUserInfo() },
                        style = MaterialTheme.typography.titleLarge.copy(textDecoration = TextDecoration.Underline)
                    )
                },
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
            // Liste des messages
            LazyColumn(
                modifier = Modifier.weight(1f)
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
                                .clickable { if (!isCurrentUser) fetchUserInfo(senderId) }
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

            // Section pour envoyer un message
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
            }
        }
    }

    // Boîte de dialogue des informations de profil
    if (isProfileDialogOpen) {
        AlertDialog(
            onDismissRequest = { isProfileDialogOpen = false },
            title = { Text("Informations de l'utilisateur") },
            text = {
                Column {
                    Text("Nom : ${userInfo["name"]}")
                    Text("Âge : ${userInfo["age"]}")
                    Text("Genre : ${userInfo["gender"]}")
                    Text("Ville : ${userInfo["city"]}")
                    Text("Sports pratiqués : ${userInfo["sports"]}")
                }
            },
            confirmButton = {
                TextButton(onClick = { isProfileDialogOpen = false }) {
                    Text("Fermer")
                }
            }
        )
    }
}