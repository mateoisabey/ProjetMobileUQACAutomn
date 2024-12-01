package com.meetch.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun ProfileScreen(onEditProfile: () -> Unit, onLogout: () -> Unit) {
    // Champs pour les informations utilisateur
    var userName by remember { mutableStateOf("") }
    var userGender by remember { mutableStateOf("") }
    var userCity by remember { mutableStateOf("") }
    var userAge by remember { mutableStateOf("") }
    var newSport by remember { mutableStateOf("") }
    var sports by remember { mutableStateOf(mutableListOf<String>()) }

    // États pour le chargement et l'édition
    var loading by remember { mutableStateOf(true) }
    var isEditing by remember { mutableStateOf(false) }

    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser

    // Charger les données utilisateur à partir de Firebase Firestore
    LaunchedEffect(Unit) {
        currentUser?.let { user ->
            db.collection("userData").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        userName = document.getString("name") ?: ""
                        sports = (document.get("sports") as? List<String>)?.toMutableList() ?: mutableListOf()
                        userGender = document.getString("gender") ?: ""
                        userCity = document.getString("city") ?: ""
                        userAge = document.getString("age") ?: ""
                    }
                    loading = false
                }
                .addOnFailureListener {
                    loading = false
                }
        }
    }

    if (loading) {
        // Indicateur de chargement pendant le téléchargement des données utilisateur
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
            Text(text = "Mon Profil", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isEditing) {
                        // Formulaire d'édition
                        OutlinedTextField(
                            value = userName,
                            onValueChange = { userName = it },
                            label = { Text("Nom") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = userAge,
                            onValueChange = { userAge = it },
                            label = { Text("Âge") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = userGender,
                            onValueChange = { userGender = it },
                            label = { Text("Sexe") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = userCity,
                            onValueChange = { userCity = it },
                            label = { Text("Ville") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Ajout de nouveaux sports
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = newSport,
                                onValueChange = { newSport = it },
                                label = { Text("Ajouter un sport") },
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
                                    containerColor = Color(0xFF6200EE),
                                    contentColor = Color.White
                                )
                            ) {
                                Text("+")
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Liste des sports avec option de suppression
                        Column(modifier = Modifier.fillMaxWidth()) {
                            sports.forEach { sport ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(text = sport, modifier = Modifier.weight(1f))
                                    IconButton(
                                        onClick = { sports.remove(sport) }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Supprimer le sport",
                                            tint = Color.Red
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Bouton pour enregistrer les modifications
                        Button(
                            onClick = {
                                currentUser?.let { user ->
                                    val updatedData = hashMapOf(
                                        "name" to userName,
                                        "age" to userAge,
                                        "gender" to userGender,
                                        "city" to userCity,
                                        "sports" to sports
                                    )
                                    db.collection("userData").document(user.uid).set(updatedData)
                                        .addOnSuccessListener {
                                            isEditing = false
                                        }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Text(text = "Enregistrer", color = Color.White)
                        }
                    } else {
                        // Affichage des informations utilisateur
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

                        Text(text = "Âge : $userAge", style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(text = "Sexe : $userGender", style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(text = "Ville : $userCity", style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { isEditing = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Text(text = "Modifier le profil", color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Bouton de déconnexion
                    Button(
                        onClick = {
                            FirebaseAuth.getInstance().signOut()
                            onLogout()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(text = "Se déconnecter", color = Color.White)
                    }
                }
            }
        }
    }
}