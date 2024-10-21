package com.meetch.ui.screen


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen(userName: String, sports: List<String>, onEditProfile: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = "Mon Profil", style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Nom : $userName", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Sports pratiqués :", style = MaterialTheme.typography.bodyLarge)
        sports.forEach { sport ->
            Text(text = sport, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onEditProfile) {
            Text(text = "Modifier le profil")
        }
    }
}