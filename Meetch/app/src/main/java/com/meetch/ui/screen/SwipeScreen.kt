package com.meetch.ui.screen

import android.annotation.SuppressLint
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import com.meetch.R
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import com.meetch.ui.data.ActivityCard

@SuppressLint("UnrememberedMutableState")
@Composable
fun SwipeScreen() {
    val activities = listOf(
        ActivityCard("Basketball", "Match de basketball", "16h", R.drawable.basket),
        ActivityCard("Football", "Session de football", "Ce soir", R.drawable.football),
        ActivityCard("Tennis", "Entraînement de tennis", "Demain", R.drawable.tennis),
        ActivityCard("Yoga", "Cours de yoga", "18h", R.drawable.yoga)
    )

    var currentIndex by remember { mutableStateOf(0) }
    var offsetX by remember { mutableStateOf(0f) }
    var isSwiping by remember { mutableStateOf(false) }
    var isAnimationCompleted by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    val animatedOffsetX by animateFloatAsState(
        targetValue = offsetX,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        finishedListener = {
            // Mettez à jour currentIndex immédiatement après que l'animation se termine
            if (offsetX > 600f || offsetX < -600f) {
                currentIndex = (currentIndex + 1).coerceAtMost(activities.size - 1)
            }
            offsetX = 0f
            isAnimationCompleted = true
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDFDFD)),
        contentAlignment = Alignment.Center
    ) {
        // Ajout du logo au-dessus des cartes
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
        )

        if (currentIndex < activities.size) {
            if (currentIndex + 1 < activities.size && isAnimationCompleted) {
                ActivityCardView(
                    activity = activities[currentIndex + 1],
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .aspectRatio(0.9f)
                        .padding(16.dp)
                        .graphicsLayer {
                            // Suppression de l'effet d'animation dans la direction opposée
                            alpha = 1f // Montre la carte suivante sans animation
                        }
                )
            }

            // Carte principale avec taille augmentée
            ActivityCardView(
                activity = activities[currentIndex],
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .aspectRatio(0.9f)
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
                                    // Fin d'animation gérée dans `finishedListener`
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
                    }
            )
        } else {
            Text(
                text = "Aucune autre activité disponible",
                style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF0C1A27)),
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
                        onClick = { onButtonClick(-700f) }, // Ignorer
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFC6302))
                    ) {
                        Text(text = "Ignorer", color = Color(0xFFFDFDFD))
                    }
                    Button(
                        onClick = { onButtonClick(700f) }, // Participer
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFC6302))
                    ) {
                        Text(text = "Participer", color = Color(0xFFFDFDFD))
                    }
                }
            }
        }
    }
}

// Fonction pour afficher la vue d'une carte d'activité
@Composable
fun ActivityCardView(activity: ActivityCard, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1A27))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Affichage de l'image
            Image(
                painter = painterResource(id = activity.imageResId),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(bottom = 8.dp)
            )

            Text(
                text = activity.title,
                style = MaterialTheme.typography.titleMedium.copy(color = Color(0xFFFDFDFD)),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = activity.description,
                style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFFDFDFD)),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = activity.time,
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFFDFDFD)),
                textAlign = TextAlign.Center
            )
        }
    }
}