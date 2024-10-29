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
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@SuppressLint("UnrememberedMutableState")
@Composable
fun SwipeScreen() {
    val activities = listOf(
        "Basketball à 16h",
        "Football ce soir",
        "Tennis demain",
        "Cours de yoga à 18h"
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
            isAnimationCompleted = true
            // Le changement de carte se fait maintenant seulement après que l'animation est totalement terminée
            if (offsetX > 600f || offsetX < -600f) {
                currentIndex++
                offsetX = 0f // Réinitialiser après le swipe complet
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