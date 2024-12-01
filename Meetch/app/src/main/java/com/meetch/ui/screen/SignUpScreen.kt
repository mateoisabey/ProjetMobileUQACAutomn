package com.meetch.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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
    var newSport by remember { mutableStateOf("") }
    var sports by remember { mutableStateOf(mutableListOf<String>()) }
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

        // Champs pour les informations utilisateur
        TextField(value = email, onValueChange = { email = it }, label = { Text("Email", color = Color.White) },
            textStyle = LocalTextStyle.current.copy(color = Color.White),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color(0xFFFF6F00),
                unfocusedIndicatorColor = Color.Gray,
                cursorColor = Color.White,
                containerColor = Color(0xFF1C2833)
            ),
            modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = password, onValueChange = { password = it }, label = { Text("Password", color = Color.White) },
            visualTransformation = PasswordVisualTransformation(),
            textStyle = LocalTextStyle.current.copy(color = Color.White),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color(0xFFFF6F00),
                unfocusedIndicatorColor = Color.Gray,
                cursorColor = Color.White,
                containerColor = Color(0xFF1C2833)
            ),
            modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = name, onValueChange = { name = it }, label = { Text("Pseudo", color = Color.White) },
            textStyle = LocalTextStyle.current.copy(color = Color.White),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color(0xFFFF6F00),
                unfocusedIndicatorColor = Color.Gray,
                cursorColor = Color.White,
                containerColor = Color(0xFF1C2833)
            ),
            modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = age, onValueChange = { age = it }, label = { Text("Age", color = Color.White) },
            textStyle = LocalTextStyle.current.copy(color = Color.White),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color(0xFFFF6F00),
                unfocusedIndicatorColor = Color.Gray,
                cursorColor = Color.White,
                containerColor = Color(0xFF1C2833)
            ),
            modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = gender, onValueChange = { gender = it }, label = { Text("Sexe", color = Color.White) },
            textStyle = LocalTextStyle.current.copy(color = Color.White),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color(0xFFFF6F00),
                unfocusedIndicatorColor = Color.Gray,
                cursorColor = Color.White,
                containerColor = Color(0xFF1C2833)
            ),
            modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = city, onValueChange = { city = it }, label = { Text("Ville", color = Color.White) },
            textStyle = LocalTextStyle.current.copy(color = Color.White),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color(0xFFFF6F00),
                unfocusedIndicatorColor = Color.Gray,
                cursorColor = Color.White,
                containerColor = Color(0xFF1C2833)
            ),
            modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))

        // Section pour ajouter des sports
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = newSport,
                onValueChange = { newSport = it },
                label = { Text("Ajouter un sport", color = Color.White) },
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color(0xFFFF6F00),
                    unfocusedIndicatorColor = Color.Gray,
                    cursorColor = Color.White,
                    containerColor = Color(0xFF1C2833)
                ),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (newSport.isNotBlank()) {
                        sports.add(newSport.trim())
                        newSport = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6F00),
                    contentColor = Color.White
                )
            ) {
                Text("+")
            }
        }

        // Liste des sports ajoutés avec option de suppression
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            sports.forEachIndexed { index, sport ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        text = sport,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { sports.removeAt(index) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Supprimer",
                            tint = Color.Red
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(colors = ButtonDefaults.buttonColors(
            containerColor = Color.Gray,
            contentColor = Color.White
        ),
            onClick = {
                authManager.signUpWithEmail(email, password) { success, user, errorMessage ->
                    if (success && user != null) {
                        val userData = hashMapOf(
                            "name" to name,
                            "age" to age,
                            "gender" to gender,
                            "city" to city,
                            "sports" to sports,
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