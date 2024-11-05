package com.meetch.ui.screen

import android.annotation.SuppressLint
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@SuppressLint("UnrememberedMutableState")
@Composable
fun SwipeScreen() {
    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val ignoredActivities = remember { mutableStateListOf<String>() }

    var activities by remember { mutableStateOf(listOf<String>()) }
    var activityIds by remember { mutableStateOf(listOf<String>()) } // Liste des IDs pour identifier les activités
    var currentIndex by remember { mutableStateOf(0) }
    var offsetX by remember { mutableStateOf(0f) }
    var isSwiping by remember { mutableStateOf(false) }
    var isAnimationCompleted by remember { mutableStateOf(true) }
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
                }
                .addOnFailureListener { exception ->
                    println("Erreur lors de la récupération des activités : ${exception.message}")
                }
        }
    }
    // Charger les activités ignorées depuis Firestore pour l'utilisateur actuel
    LaunchedEffect(Unit) {
        currentUser?.let { user ->
            db.collection("users").document(user.uid).collection("ignoredActivities")
                .get()
                .addOnSuccessListener { result ->
                    ignoredActivities.addAll(result.documents.map { it.id })
                    loadActivities() // Charger les activités après avoir récupéré les ignorées
                }
                .addOnFailureListener { exception ->
                    println("Erreur lors de la récupération des activités ignorées : ${exception.message}")
                    loadActivities() // Charger les activités même en cas d'échec de récupération des ignorées
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

    val animatedOffsetX by animateFloatAsState(
        targetValue = offsetX,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        finishedListener = {
            isAnimationCompleted = true
            if (offsetX > 600f || offsetX < -600f) {
                if (offsetX < 0) {
                    val ignoredActivityId = activityIds[currentIndex]
                    ignoredActivities.add(ignoredActivityId)
                    saveIgnoredActivity(ignoredActivityId)
                }
                currentIndex++
                offsetX = 0f
            }
            isSwiping = false
        }
    )

    val rotationDegrees by derivedStateOf { (animatedOffsetX / 30).coerceIn(-25f, 25f) }

    fun onButtonClick(targetOffset: Float) {
        if (isAnimationCompleted) {
            coroutineScope.launch {
                isSwiping = true
                isAnimationCompleted = false
                offsetX = targetOffset
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (currentIndex < activities.size) {
            if (currentIndex + 1 < activities.size) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .aspectRatio(1f)
                        .padding(16.dp)
                        .graphicsLayer {
                            alpha = (offsetX.absoluteValue / 600f).coerceIn(0f, 1f)
                        },
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Text(
                        text = activities[currentIndex + 1],
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .aspectRatio(1f)
                    .offset {
                        IntOffset(
                            x = when {
                                offsetX > 600f -> (animatedOffsetX + 400f).roundToInt()
                                offsetX < -600f -> (animatedOffsetX - 400f).roundToInt()
                                else -> animatedOffsetX.roundToInt()
                            },
                            y = 0
                        )
                    }
                    .graphicsLayer {
                        rotationZ = rotationDegrees
                        alpha = 1f - (animatedOffsetX.absoluteValue / 1000f).coerceIn(0f, 1f)
                    }
                    .padding(16.dp)
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onDragStart = {
                                if (isAnimationCompleted) {
                                    isSwiping = true
                                }
                            },
                            onDragEnd = {
                                if (offsetX > 600f || offsetX < -600f) {
                                    // Laisser l'animation de sortie se dérouler
                                } else {
                                    offsetX = 0f
                                }
                                isSwiping = false
                            },
                            onHorizontalDrag = { _, dragAmount ->
                                if (isAnimationCompleted) {
                                    offsetX += dragAmount
                                }
                            }
                        )
                    },
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Text(
                    text = activities[currentIndex],
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
                        onClick = { onButtonClick(-700f) }
                    ) {
                        Text(text = "Ignorer")
                    }
                    Button(
                        onClick = { onButtonClick(700f) }
                    ) {
                        Text(text = "Participer")
                    }
                }
            }
        }
    }
}