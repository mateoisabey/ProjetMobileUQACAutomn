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
import androidx.navigation.NavController
import com.meetch.R
import com.meetch.auth.FirebaseAuthManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(authManager: FirebaseAuthManager, navController: NavController, onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

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

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email", color = Color.White) },
            textStyle = LocalTextStyle.current.copy(color = Color.White), // Couleur du texte saisi
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color(0xFFFF6F00),  // Couleur de la ligne sous le champ lorsqu'il est focalisé
                unfocusedIndicatorColor = Color.Gray,       // Couleur de la ligne sous le champ lorsqu'il n'est pas focalisé
                cursorColor = Color.White,                  // Couleur du curseur
                containerColor = Color(0xFF1C2833)          // Couleur de fond du TextField
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password", color = Color.White) },
            visualTransformation = PasswordVisualTransformation(),
            textStyle = LocalTextStyle.current.copy(color = Color.White), // Couleur du texte saisi
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color(0xFFFF6F00),  // Couleur de la ligne sous le champ lorsqu'il est focalisé
                unfocusedIndicatorColor = Color.Gray,       // Couleur de la ligne sous le champ lorsqu'il n'est pas focalisé
                cursorColor = Color.White,                  // Couleur du curseur
                containerColor = Color(0xFF1C2833)          // Couleur de fond du TextField
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(colors = ButtonDefaults.buttonColors(
            containerColor = Color.Gray,   // Fond du bouton en blanc
            contentColor = Color.White      // Texte en noir pour contraste
        ),
            onClick = {
            authManager.loginWithEmail(email, password) { success, _, errorMessage ->
                if (success) {
                    onLoginSuccess()
                } else {
                    message = "Échec de la connexion : $errorMessage"
                }
            }
        },
        ) {
            Text("Connexion", color = Color.White)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = message, color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = {
            navController.navigate("sign_up")
        }) {
            Text("Vous n'avez pas de compte ? Inscrivez-vous", color = Color.White)
        }
    }
}