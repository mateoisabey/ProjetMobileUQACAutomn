package com.meetch.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.text.style.TextAlign
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import com.meetch.R
@SuppressLint("UnrememberedMutableState")
@Composable
fun SwipeScreen() {
    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val ignoredActivities = remember { mutableStateListOf<String>() }
    val acceptedActivities = remember { mutableStateListOf<String>() }

    var activities by remember { mutableStateOf(listOf<String>()) }
    var activityIds by remember { mutableStateOf(listOf<String>()) }
    var creatorIds by remember { mutableStateOf(listOf<String>()) }
    var dates by remember { mutableStateOf(listOf<String>()) }
    var descriptions by remember { mutableStateOf(listOf<String>()) }
    var locations by remember { mutableStateOf(listOf<String>()) }

    var currentIndex by remember { mutableStateOf(0) }
    var offsetX by remember { mutableStateOf(0f) }

    // Fonction pour charger les activités avec un listener en temps réel
    fun loadActivitiesWithListener() {
        currentUser?.let { user ->
            db.collection("activities")
                .whereNotEqualTo("userId", user.uid)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        println("Erreur lors de la récupération des activités : ${e.message}")
                        return@addSnapshotListener
                    }

                    val filteredActivities = snapshot?.documents?.filterNot { document ->
                        ignoredActivities.contains(document.id) || acceptedActivities.contains(document.id)
                    } ?: emptyList()

                    activities = filteredActivities.map { it.getString("name") ?: "" }
                    activityIds = filteredActivities.map { it.id }
                    creatorIds = filteredActivities.map { it.getString("userId") ?: "" }
                    dates = filteredActivities.map { it.getString("date") ?: "" }
                    descriptions = filteredActivities.map { it.getString("description") ?: "" }
                    locations = filteredActivities.map { it.getString("location") ?: "" }

                    // Réinitialiser l'index si nécessaire
                    currentIndex = 0
                }
        }
    }

    LaunchedEffect(Unit) {
        currentUser?.let { user ->
            // Charger les activités ignorées et acceptées
            db.collection("users").document(user.uid).collection("ignoredActivities")
                .get()
                .addOnSuccessListener { result ->
                    ignoredActivities.addAll(result.documents.map { it.id })
                    loadActivitiesWithListener()
                }
                .addOnFailureListener { exception ->
                    println("Erreur lors de la récupération des activités ignorées : ${exception.message}")
                    loadActivitiesWithListener()
                }

            db.collection("users").document(user.uid).collection("acceptedActivities")
                .get()
                .addOnSuccessListener { result ->
                    acceptedActivities.addAll(result.documents.map { it.id })
                    loadActivitiesWithListener()
                }
                .addOnFailureListener { exception ->
                    println("Erreur lors de la récupération des activités acceptées : ${exception.message}")
                    loadActivitiesWithListener()
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

    fun saveAcceptedActivity(activityId: String) {
        currentUser?.let { user ->
            db.collection("users").document(user.uid).collection("acceptedActivities")
                .document(activityId)
                .set(mapOf("accepted" to true))
                .addOnFailureListener { exception ->
                    println("Erreur lors de l'ajout de l'activité acceptée : ${exception.message}")
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
        val acceptedActivityId = activityIds[currentIndex]
        acceptedActivities.add(acceptedActivityId)
        saveAcceptedActivity(acceptedActivityId)
        sendMessageRequest(acceptedActivityId, creatorIds[currentIndex])
        currentIndex++
        offsetX = 0f
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1B2A)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo de Meetch",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(top = 16.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (currentIndex < activities.size) {
                val activityName = activities[currentIndex]
                val activityDate = dates[currentIndex]
                val activityDescription = descriptions[currentIndex]
                val activityLocation = locations[currentIndex]

                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .aspectRatio(0.75f)
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
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFF6F00))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = activityName,
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "Lieu : $activityLocation\nDate : $activityDate\n\nDescription : $activityDescription",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            } else {
                Text(
                    text = "Aucune autre activité disponible",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        if (currentIndex < activities.size) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(
                    onClick = { swipeLeft() },
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Ignorer",
                        tint = Color.Red,
                        modifier = Modifier.size(48.dp)
                    )
                }
                IconButton(
                    onClick = { swipeRight() },
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Participer",
                        tint = Color.Green,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }
    }
}