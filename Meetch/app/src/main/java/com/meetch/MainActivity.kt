package com.meetch

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.meetch.ui.RegisterActivity  // Importer RegisterActivity depuis le package com.meetch.ui
import com.meetch.ui.theme.MeetchTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MeetchTheme {
                LoginScreen(onRegisterClick = {
                    // Redirige vers RegisterActivity
                    val intent = Intent(this, RegisterActivity::class.java)
                    startActivity(intent)
                })
            }
        }
    }
}

@Composable
fun LoginScreen(onRegisterClick: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Connexion")
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mot de passe") }
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { /* Logique de connexion */ }) {
            Text(text = "Se connecter")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { onRegisterClick() }) {
            Text(text = "Créer un compte")
        }
    }
}