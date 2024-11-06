package com.meetch.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.meetch.R
import com.meetch.auth.FirebaseAuthManager
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
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
            .background(Color(0xFF0D1B2A)) // Couleur de fond
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo en haut de l'écran
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo de Meetch",
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(bottom = 24.dp),
            alignment = Alignment.Center
        )

        TextField(value = email, onValueChange = { email = it }, label = { Text("Email", color = Color.White) },
            textStyle = LocalTextStyle.current.copy(color = Color.White), // Couleur du texte saisi
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color(0xFFFF6F00),  // Couleur de la ligne sous le champ lorsqu'il est focalisé
                unfocusedIndicatorColor = Color.Gray,       // Couleur de la ligne sous le champ lorsqu'il n'est pas focalisé
                cursorColor = Color.White,                  // Couleur du curseur
                containerColor = Color(0xFF1C2833)          // Couleur de fond du TextField
            ),
            modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = password, onValueChange = { password = it }, label = { Text("Password", color = Color.White) },
            visualTransformation = PasswordVisualTransformation(),
            textStyle = LocalTextStyle.current.copy(color = Color.White), // Couleur du texte saisi
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color(0xFFFF6F00),  // Couleur de la ligne sous le champ lorsqu'il est focalisé
                unfocusedIndicatorColor = Color.Gray,       // Couleur de la ligne sous le champ lorsqu'il n'est pas focalisé
                cursorColor = Color.White,                  // Couleur du curseur
                containerColor = Color(0xFF1C2833)          // Couleur de fond du TextField
            ),
            modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = name, onValueChange = { name = it }, label = { Text("Nom", color = Color.White) },
            textStyle = LocalTextStyle.current.copy(color = Color.White), // Couleur du texte saisi
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color(0xFFFF6F00),  // Couleur de la ligne sous le champ lorsqu'il est focalisé
                unfocusedIndicatorColor = Color.Gray,       // Couleur de la ligne sous le champ lorsqu'il n'est pas focalisé
                cursorColor = Color.White,                  // Couleur du curseur
                containerColor = Color(0xFF1C2833)          // Couleur de fond du TextField
            ),
            modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = age, onValueChange = { age = it }, label = { Text("Age", color = Color.White) },
            textStyle = LocalTextStyle.current.copy(color = Color.White), // Couleur du texte saisi
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color(0xFFFF6F00),  // Couleur de la ligne sous le champ lorsqu'il est focalisé
                unfocusedIndicatorColor = Color.Gray,       // Couleur de la ligne sous le champ lorsqu'il n'est pas focalisé
                cursorColor = Color.White,                  // Couleur du curseur
                containerColor = Color(0xFF1C2833)          // Couleur de fond du TextField
            ),
            modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = gender, onValueChange = { gender = it }, label = { Text("Sexe", color = Color.White) },
            textStyle = LocalTextStyle.current.copy(color = Color.White), // Couleur du texte saisi
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color(0xFFFF6F00),  // Couleur de la ligne sous le champ lorsqu'il est focalisé
                unfocusedIndicatorColor = Color.Gray,       // Couleur de la ligne sous le champ lorsqu'il n'est pas focalisé
                cursorColor = Color.White,                  // Couleur du curseur
                containerColor = Color(0xFF1C2833)          // Couleur de fond du TextField
            ),
            modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = city, onValueChange = { city = it }, label = { Text("Ville", color = Color.White) },
            textStyle = LocalTextStyle.current.copy(color = Color.White), // Couleur du texte saisi
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color(0xFFFF6F00),  // Couleur de la ligne sous le champ lorsqu'il est focalisé
                unfocusedIndicatorColor = Color.Gray,       // Couleur de la ligne sous le champ lorsqu'il n'est pas focalisé
                cursorColor = Color.White,                  // Couleur du curseur
                containerColor = Color(0xFF1C2833)          // Couleur de fond du TextField
            ),
            modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = sports, onValueChange = { sports = it }, label = { Text("Sports pratiqués (séparés par des virgules)", color = Color.White) },
            textStyle = LocalTextStyle.current.copy(color = Color.White),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color(0xFFFF6F00),
                unfocusedIndicatorColor = Color.Gray,
                cursorColor = Color.White,
                containerColor = Color(0xFF1C2833)          // Couleur de fond du TextField
            ),
            modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        Button(colors = ButtonDefaults.buttonColors(
            containerColor = Color.Gray,   // Fond du bouton en blanc
            contentColor = Color.White      // Texte en noir pour contraste
        ),
            onClick = {
            authManager.signUpWithEmail(email, password) { success, user, errorMessage ->
                if (success && user != null) {
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
            Text("Créer un compte", color = Color.White)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = message, color = Color.White)
    }
}