package com.meetch.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SwipeScreen() {
    val activities = listOf(
        "Basketball à 16h",
        "Football ce soir",
        "Tennis demain",
        "Cours de yoga à 18h"
    )
    var currentIndex by remember { mutableStateOf(0) }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (currentIndex < activities.size) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .aspectRatio(1f)
                    .padding(16.dp)
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
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { currentIndex++ }) {
                    Text(text = "Ignorer")
                }
                Button(onClick = { currentIndex++ }) {
                    Text(text = "Participer")
                }
            }
        } else {
            Text(text = "Aucune autre activité disponible", style = MaterialTheme.typography.bodyLarge)
        }
    }
}