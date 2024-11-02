package com.meetch.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ProfileScreen(onEditProfile: () -> Unit) {
    var userName by remember { mutableStateOf("") }
    var userGender by remember { mutableStateOf("") }
    var userCity by remember { mutableStateOf("") }
    var userAge by remember { mutableStateOf("") }
    var sports by remember { mutableStateOf(listOf<String>()) }
    var loading by remember { mutableStateOf(true) }

    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser

    LaunchedEffect(Unit) {
        currentUser?.let { user ->
            db.collection("userData").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        userName = document.getString("name") ?: ""
                        sports = document.get("sports") as? List<String> ?: listOf()
                        userGender = document.getString("gender") ?: ""
                        userCity = document.getString("city") ?: ""
                        userAge = document.getString("age") ?: ""
                    }
                    loading = false
                }
                .addOnFailureListener {
                    loading = false
                    // Gérer l'erreur ici si besoin
                }
        }
    }

    if (loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
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
            if (sports.isNotEmpty()) {
                sports.forEach { sport ->
                    Text(text = sport, style = MaterialTheme.typography.bodySmall)
                }
            } else {
                Text(text = "Aucun sport enregistré", style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Age : $userAge", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Sexe : $userGender", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Ville : $userCity", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = onEditProfile) {
                Text(text = "Modifier le profil")
            }
        }
    }
}
