package com.meetch.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@SuppressLint("UnrememberedMutableState")
@Composable
fun SwipeScreen() {
    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val ignoredActivities = remember { mutableStateListOf<String>() }

    var activities by remember { mutableStateOf(listOf<String>()) }
    var activityIds by remember { mutableStateOf(listOf<String>()) }
    var creatorIds by remember { mutableStateOf(listOf<String>()) }
    var currentIndex by remember { mutableStateOf(0) }
    var offsetX by remember { mutableStateOf(0f) }
    val coroutineScope = rememberCoroutineScope()

    fun loadActivities() {
        currentUser?.let { user ->
            db.collection("activities")
                .whereNotEqualTo("userId", user.uid)
                .get()
                .addOnSuccessListener { result ->
                    val filteredActivities = result.documents.filterNot { document ->
                        ignoredActivities.contains(document.id)
                    }
                    activities = filteredActivities.map { it.getString("name") ?: "" }
                    activityIds = filteredActivities.map { it.id }
                    creatorIds = filteredActivities.map { it.getString("userId") ?: "" }
                }
                .addOnFailureListener { exception ->
                    println("Erreur lors de la récupération des activités : ${exception.message}")
                }
        }
    }

    LaunchedEffect(Unit) {
        currentUser?.let { user ->
            db.collection("users").document(user.uid).collection("ignoredActivities")
                .get()
                .addOnSuccessListener { result ->
                    ignoredActivities.addAll(result.documents.map { it.id })
                    loadActivities()
                }
                .addOnFailureListener { exception ->
                    println("Erreur lors de la récupération des activités ignorées : ${exception.message}")
                    loadActivities()
                }
        }
    }

    fun saveIgnoredActivity(activityId: String) {
        currentUser?.let { user ->
            db.collection("users").document(user.uid).collection("ignoredActivities")
                .document(activityId)
                .set(mapOf("ignored" to true))
                .addOnFailureListener { exception ->
                    println("Erreur lors de l'ajout de l'activité ignorée : ${exception.message}")
                }
        }
    }

    fun sendMessageRequest(activityId: String, creatorId: String) {
        currentUser?.let { user ->
            val request = mapOf(
                "activityId" to activityId,
                "fromUserId" to user.uid,
                "toUserId" to creatorId,
                "timestamp" to System.currentTimeMillis()
            )
            db.collection("messageRequests").add(request)
                .addOnSuccessListener { println("Demande de message envoyée") }
                .addOnFailureListener { e -> println("Erreur : ${e.message}") }
        }
    }

    fun swipeLeft() {
        val ignoredActivityId = activityIds[currentIndex]
        ignoredActivities.add(ignoredActivityId)
        saveIgnoredActivity(ignoredActivityId)
        currentIndex++
        offsetX = 0f
    }

    fun swipeRight() {
        sendMessageRequest(activityIds[currentIndex], creatorIds[currentIndex])
        currentIndex++
        offsetX = 0f
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (currentIndex < activities.size) {
            val activityName = activities[currentIndex]

            Card(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .aspectRatio(1f)
                    .offset { IntOffset(offsetX.toInt(), 0) }
                    .padding(16.dp)
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                if (offsetX > 600f) {
                                    swipeRight()
                                } else if (offsetX < -600f) {
                                    swipeLeft()
                                } else {
                                    offsetX = 0f
                                }
                            },
                            onHorizontalDrag = { _, dragAmount ->
                                offsetX += dragAmount
                            }
                        )
                    },
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Text(
                    text = activityName,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            Text(
                text = "Aucune autre activité disponible",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )
        }

        if (currentIndex < activities.size) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { swipeLeft() }
                    ) {
                        Text(text = "Ignorer")
                    }
                    Button(
                        onClick = { swipeRight() }
                    ) {
                        Text(text = "Participer")
                    }
                }
            }
        }
    }
}