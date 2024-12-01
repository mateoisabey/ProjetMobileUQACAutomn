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
    // États pour l'email, le mot de passe et les messages d'erreur ou de succès
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    // Mise en page principale
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1B2A)) // Couleur de fond sombre
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Affichage du logo de l'application
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo de Meetch",
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(bottom = 24.dp),
            alignment = Alignment.Center
        )

        // Champ de saisie pour l'email
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email", color = Color.White) },
            textStyle = LocalTextStyle.current.copy(color = Color.White), // Texte en blanc
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color(0xFFFF6F00),  // Couleur orange pour le focus
                unfocusedIndicatorColor = Color.Gray,       // Couleur grise lorsque non focalisé
                cursorColor = Color.White,                  // Curseur blanc
                containerColor = Color(0xFF1C2833)          // Couleur de fond du champ
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Champ de saisie pour le mot de passe
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password", color = Color.White) },
            visualTransformation = PasswordVisualTransformation(), // Masquer le texte saisi
            textStyle = LocalTextStyle.current.copy(color = Color.White), // Texte en blanc
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color(0xFFFF6F00),  // Couleur orange pour le focus
                unfocusedIndicatorColor = Color.Gray,       // Couleur grise lorsque non focalisé
                cursorColor = Color.White,                  // Curseur blanc
                containerColor = Color(0xFF1C2833)          // Couleur de fond du champ
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Bouton de connexion
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Gray,   // Fond gris du bouton
                contentColor = Color.White     // Texte blanc
            ),
            onClick = {
                // Lancer la connexion avec FirebaseAuth
                authManager.loginWithEmail(email, password) { success, _, errorMessage ->
                    if (success) {
                        onLoginSuccess() // Appeler le callback si connexion réussie
                    } else {
                        message = "Échec de la connexion : $errorMessage" // Afficher un message d'erreur
                    }
                }
            },
        ) {
            Text("Connexion", color = Color.White)
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Affichage du message d'erreur ou de succès
        Text(text = message, color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))

        // Lien pour naviguer vers l'écran d'inscription
        TextButton(onClick = {
            navController.navigate("sign_up") // Naviguer vers l'écran d'inscription
        }) {
            Text("Vous n'avez pas de compte ? Inscrivez-vous", color = Color.White)
        }
    }
}