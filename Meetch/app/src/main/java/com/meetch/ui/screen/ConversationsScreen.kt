package com.meetch.ui.screen

import androidx.compose.foundation.background
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
    var unreadConversations by remember { mutableStateOf(setOf<String>()) } // Conserve les IDs des conversations avec des messages non lus

    // Charger les requêtes de messages en temps réel
    LaunchedEffect(Unit) {
        currentUser?.let { user ->
            // Listener pour les requêtes de messages
            db.collection("messageRequests")
                .whereEqualTo("toUserId", user.uid)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        println("Erreur lors de la récupération des requêtes de messages : ${e.message}")
                        return@addSnapshotListener
                    }

                    val requestsList = mutableListOf<Triple<String, String, String>>()
                    snapshot?.documents?.forEach { document ->
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

            // Listener pour les conversations existantes
            db.collection("conversations")
                .whereArrayContains("participants", user.uid)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        println("Erreur lors de la récupération des conversations : ${e.message}")
                        return@addSnapshotListener
                    }

                    val conversationsList = mutableListOf<Triple<String, String, String>>()
                    val unreadList = mutableSetOf<String>()

                    snapshot?.documents?.forEach { document ->
                        val conversationId = document.id
                        val fromUserId = document.getString("fromUserId") ?: ""
                        val toUserId = document.getString("toUserId") ?: ""

                        // Déterminer si la conversation a des messages non lus
                        val lastReadTimestamp = document.getLong("lastReadTimestamp_${user.uid}") ?: 0L
                        val lastMessageTimestamp = document.getLong("lastMessageTimestamp") ?: 0L
                        if (lastMessageTimestamp > lastReadTimestamp) {
                            unreadList.add(conversationId)
                        }

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
                    unreadConversations = unreadList
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
                        .background(if (unreadConversations.contains(conversationId)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                        .clickable {
                            navController.navigate("conversationDetail/$conversationTitle/$conversationId/$fromUserId")
                            // Marquer la conversation comme lue
                            db.collection("conversations").document(conversationId)
                                .update("lastReadTimestamp_${currentUser?.uid}", System.currentTimeMillis())
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