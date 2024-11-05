package com.meetch.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.meetch.auth.FirebaseAuthManager

@Composable
fun LoginScreen(authManager: FirebaseAuthManager, navController: NavController, onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

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
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            authManager.loginWithEmail(email, password) { success, _, errorMessage ->
                if (success) {
                    println("Connexion réussie")
                    onLoginSuccess()
                } else {
                    println("Échec de la connexion : $errorMessage")
                    message = "Échec de la connexion : $errorMessage"
                }
            }
        }) {
            Text("Connexion")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = message)
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = {
            navController.navigate("sign_up")
        }) {
            Text("Vous n'avez pas de compte ? Inscrivez-vous")
        }
    }
}
