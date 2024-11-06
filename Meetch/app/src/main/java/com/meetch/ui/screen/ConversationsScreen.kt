package com.meetch.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ConversationsScreen(navController: NavHostController) {
    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser

    // Sections distinctes : requêtes de messages et conversations existantes
    var messageRequests by remember { mutableStateOf(listOf<Triple<String, String, String>>()) }
    var existingConversations by remember { mutableStateOf(listOf<Triple<String, String, String>>()) }

    LaunchedEffect(Unit) {
        currentUser?.let { user ->
            val requestsList = mutableListOf<Triple<String, String, String>>()
            val conversationsList = mutableListOf<Triple<String, String, String>>()

            // Charger les requêtes de messages
            db.collection("messageRequests")
                .whereEqualTo("toUserId", user.uid)
                .get()
                .addOnSuccessListener { result ->
                    result.documents.forEach { document ->
                        val fromUserId = document.getString("fromUserId") ?: ""
                        val activityId = document.getString("activityId") ?: ""
                        val messageRequestId = document.id

                        db.collection("userData").document(fromUserId)
                            .get()
                            .addOnSuccessListener { userDoc ->
                                val userName = userDoc.getString("name") ?: "Utilisateur inconnu"
                                db.collection("activities").document(activityId)
                                    .get()
                                    .addOnSuccessListener { activityDoc ->
                                        val activityName = activityDoc.getString("name") ?: "Activité inconnue"
                                        requestsList.add(
                                            Triple("$userName pour l'activité $activityName", messageRequestId, fromUserId)
                                        )
                                        messageRequests = requestsList.toList()
                                    }
                                    .addOnFailureListener { exception ->
                                        println("Erreur lors de la récupération de l'activité : ${exception.message}")
                                    }
                            }
                            .addOnFailureListener { exception ->
                                println("Erreur lors de la récupération de l'utilisateur : ${exception.message}")
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    println("Erreur lors de la récupération des demandes de messages : ${exception.message}")
                }

            // Charger les conversations existantes
            db.collection("conversations")
                .whereArrayContains("participants", user.uid)
                .get()
                .addOnSuccessListener { result ->
                    result.documents.forEach { document ->
                        val fromUserId = document.getString("fromUserId") ?: ""
                        val toUserId = document.getString("toUserId") ?: ""
                        val conversationId = document.id

                        // Déterminer le nom d'affichage pour la conversation
                        db.collection("userData").document(if (fromUserId == user.uid) toUserId else fromUserId)
                            .get()
                            .addOnSuccessListener { userDoc ->
                                val otherUserName = userDoc.getString("name") ?: "Utilisateur"
                                conversationsList.add(Triple(otherUserName, conversationId, fromUserId))
                                existingConversations = conversationsList.toList()
                            }
                            .addOnFailureListener { exception ->
                                println("Erreur lors de la récupération des informations de l'utilisateur : ${exception.message}")
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    println("Erreur lors de la récupération des conversations : ${exception.message}")
                }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Mes Conversations", style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(16.dp))

        // Section pour les requêtes de messages
        Text("Requêtes de Messages", style = MaterialTheme.typography.bodyLarge)
        LazyColumn {
            items(messageRequests.size) { index ->
                val (requestTitle, messageRequestId, fromUserId) = messageRequests[index]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            navController.navigate("conversationDetail/$requestTitle/$messageRequestId/$fromUserId")
                        }
                ) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = requestTitle,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Button(onClick = {
                            // Accepter la requête : déplacer vers "conversations"
                            db.collection("conversations").document(messageRequestId)
                                .set(mapOf("fromUserId" to fromUserId, "toUserId" to currentUser?.uid, "participants" to listOf(fromUserId, currentUser?.uid)))
                                .addOnSuccessListener {
                                    db.collection("messageRequests").document(messageRequestId).delete()
                                        .addOnSuccessListener {
                                            // Mettre à jour les listes
                                            messageRequests = messageRequests.filterNot { it.second == messageRequestId }
                                            existingConversations = existingConversations + Triple(requestTitle, messageRequestId, fromUserId)
                                        }
                                        .addOnFailureListener { exception ->
                                            println("Erreur lors de la suppression de la requête : ${exception.message}")
                                        }
                                }
                                .addOnFailureListener { exception ->
                                    println("Erreur lors de l'acceptation de la requête : ${exception.message}")
                                }
                        }) {
                            Text("Accepter")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Section pour les conversations existantes
        Text("Conversations", style = MaterialTheme.typography.bodyLarge)
        LazyColumn {
            items(existingConversations.size) { index ->
                val (conversationTitle, conversationId, fromUserId) = existingConversations[index]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            navController.navigate("conversationDetail/$conversationTitle/$conversationId/$fromUserId")
                        }
                ) {
                    Text(
                        text = conversationTitle,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}