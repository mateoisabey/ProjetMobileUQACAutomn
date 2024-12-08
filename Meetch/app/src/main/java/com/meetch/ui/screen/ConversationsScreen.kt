package com.meetch.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ConversationsScreen(navController: NavHostController) {
    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser

    // Liste des requêtes de messages
    var messageRequests by remember { mutableStateOf(listOf<Triple<String, String, String>>()) }

    // Liste des conversations existantes
    var existingConversations by remember { mutableStateOf(listOf<Triple<String, String, String>>()) }

    // Ensemble des conversations avec des messages non lus
    var unreadConversations by remember { mutableStateOf(setOf<String>()) }

    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        currentUser?.let { user ->
            // Écouteur pour les requêtes de messages
            db.collection("messageRequests")
                .whereEqualTo("toUserId", user.uid)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        println("Erreur lors de la récupération des requêtes de messages : ${e.message}")
                        return@addSnapshotListener
                    }

                    val requestsList = mutableListOf<Pair<Long, Triple<String, String, String>>>()
                    snapshot?.documents?.forEach { document ->
                        val fromUserId = document.getString("fromUserId") ?: ""
                        val activityId = document.getString("activityId") ?: ""
                        val messageRequestId = document.id
                        val timestamp = document.getLong("timestamp") ?: 0L

                        // Charger les informations de l'utilisateur et de l'activité
                        db.collection("userData").document(fromUserId)
                            .get()
                            .addOnSuccessListener { userDoc ->
                                val userName = userDoc.getString("name") ?: "Utilisateur inconnu"
                                db.collection("activities").document(activityId)
                                    .get()
                                    .addOnSuccessListener { activityDoc ->
                                        val activityName = activityDoc.getString("name") ?: "Activité inconnue"
                                        requestsList.add(
                                            timestamp to Triple(
                                                "$userName pour l'activité ${activityName.split(" ").drop(1).joinToString(" ")}",
                                                messageRequestId,
                                                fromUserId
                                            )
                                        )
                                        messageRequests = requestsList.sortedByDescending { it.first }.map { it.second }
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

            // Écouteur pour les conversations existantes
            db.collection("conversations")
                .whereArrayContains("participants", user.uid)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        println("Erreur lors de la récupération des conversations : ${e.message}")
                        return@addSnapshotListener
                    }

                    val conversationsList = mutableListOf<Pair<Long, Triple<String, String, String>>>()
                    val unreadList = mutableSetOf<String>()

                    snapshot?.documents?.forEach { document ->
                        val conversationId = document.id
                        val fromUserId = document.getString("fromUserId") ?: ""
                        val toUserId = document.getString("toUserId") ?: ""
                        val lastMessageTimestamp = document.getLong("lastMessageTimestamp") ?: 0L
                        val nomActivite = document.getString("nomActivite") ?: "Activité inconnue"

                        // Vérifier si la conversation a des messages non lus
                        val lastReadTimestamp = document.getLong("lastReadTimestamp_${user.uid}") ?: 0L
                        if (lastMessageTimestamp > lastReadTimestamp) {
                            unreadList.add(conversationId)
                        }

                        // Identifier l'autre utilisateur pour la conversation
                        val otherUserId = if (fromUserId == user.uid) toUserId else fromUserId

                        db.collection("userData").document(otherUserId)
                            .get()
                            .addOnSuccessListener { userDoc ->
                                val otherUserName = userDoc.getString("name") ?: "Utilisateur inconnu"
                                conversationsList.add(
                                    lastMessageTimestamp to Triple(
                                        "$otherUserName pour l'activité ${nomActivite.split(" ").drop(1).joinToString(" ")}",
                                        conversationId,
                                        fromUserId
                                    )
                                )
                                existingConversations =
                                    conversationsList.sortedByDescending { it.first }.map { it.second }
                            }
                            .addOnFailureListener { exception ->
                                println("Erreur lors de la récupération des informations de l'utilisateur : ${exception.message}")
                            }
                    }
                    unreadConversations = unreadList
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        // Titre principal
        Text("Mes Conversations", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        // Section des requêtes de messages
        Text("Requêtes de Messages", style = MaterialTheme.typography.bodyLarge)
        LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
            items(messageRequests.size) { index ->
                val (requestTitle, messageRequestId, fromUserId) = messageRequests[index]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            navController.navigate("conversationDetail/$requestTitle/$messageRequestId/$fromUserId")
                        },
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = requestTitle,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Button(onClick = {
                            db.collection("conversations").document(messageRequestId)
                                .set(
                                    mapOf(
                                        "fromUserId" to fromUserId,
                                        "toUserId" to currentUser?.uid,
                                        "nomActivite" to requestTitle.split(" ").drop(1).joinToString(" "),
                                        "participants" to listOf(fromUserId, currentUser?.uid),
                                        "lastMessageTimestamp" to System.currentTimeMillis()
                                    )
                                )
                                .addOnSuccessListener {
                                    db.collection("messageRequests").document(messageRequestId).delete()
                                        .addOnSuccessListener {
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

        // Section des conversations existantes
        Text("Conversations", style = MaterialTheme.typography.bodyLarge)
        LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
            items(existingConversations.size) { index ->
                val (conversationTitle, conversationId, fromUserId) = existingConversations[index]
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate("conversationDetail/$conversationTitle/$conversationId/$fromUserId")
                                db.collection("conversations").document(conversationId)
                                    .update("lastReadTimestamp_${currentUser?.uid}", System.currentTimeMillis())
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (unreadConversations.contains(conversationId)) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(Color.Red)
                                    .padding(end = 8.dp)
                            )
                        }
                        Text(
                            text = conversationTitle,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    Divider(modifier = Modifier.padding(top = 4.dp))
                }
            }
        }
    }
}