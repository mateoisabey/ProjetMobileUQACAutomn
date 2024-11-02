package com.meetch.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.meetch.auth.FirebaseAuthManager
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun SignUpScreen(authManager: FirebaseAuthManager, onSignUpSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var sports by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    val db = FirebaseFirestore.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = name, onValueChange = { name = it }, label = { Text("Nom") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = age, onValueChange = { age = it }, label = { Text("Age") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = gender, onValueChange = { gender = it }, label = { Text("Sexe") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = city, onValueChange = { city = it }, label = { Text("Ville") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = sports, onValueChange = { sports = it }, label = { Text("Sports pratiqués (séparés par des virgules)") })
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            authManager.signUpWithEmail(email, password) { success, user, errorMessage ->
                if (success && user != null) {
                    // Ajouter les informations utilisateur dans Firestore
                    val userData = hashMapOf(
                        "name" to name,
                        "age" to age,
                        "gender" to gender,
                        "city" to city,
                        "sports" to sports.split(",").map { it.trim() },
                        "email" to email
                    )

                    db.collection("userData").document(user.uid)
                        .set(userData)
                        .addOnSuccessListener {
                            message = "Utilisateur créé avec succès"
                            onSignUpSuccess()
                        }
                        .addOnFailureListener { e ->
                            message = "Erreur lors de l'enregistrement des données : ${e.message}"
                        }
                } else {
                    message = "Échec de la création du compte : $errorMessage"
                }
            }
        }) {
            Text("Créer un compte")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = message)
    }
}
