package com.meetch.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.meetch.ui.theme.MeetchTheme
import com.meetch.ui.viewModel.RegisterViewModel


class RegisterActivity : ComponentActivity() {
    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MeetchTheme {
                RegisterScreen(viewModel = registerViewModel)
            }
        }
    }
}

@Composable
fun RegisterScreen(viewModel: RegisterViewModel) {
    val navController = rememberNavController()

    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues -> // Utilisation de paddingValues
        NavHost(
            navController = navController,
            startDestination = "step1",
            modifier = Modifier.padding(paddingValues) // Appliquer le padding
        )  {
            composable("step1") {
                Step1Screen(navController = navController, viewModel = viewModel)
            }
            composable("step2") {
                Step2Screen(navController = navController, viewModel = viewModel)
            }
            composable("step3") {
                Step3Screen(navController = navController, viewModel = viewModel)
            }
            composable("step4") {
                Step4Screen(navController = navController, viewModel = viewModel)
            }
            composable("summary") {
                SummaryScreen(navController = navController, viewModel = viewModel)
            }
        }
    }
}
    @Composable
    fun Step1Screen(navController: NavHostController, viewModel: RegisterViewModel) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Étape 1 : Informations personnelles")
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = viewModel.firstName,
                onValueChange = { viewModel.firstName = it },
                label = { Text("Prénom") }
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = viewModel.lastName,
                onValueChange = { viewModel.lastName = it },
                label = { Text("Nom") }
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = viewModel.pseudo,
                onValueChange = { viewModel.pseudo = it },
                label = { Text("Pseudonyme") }
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = viewModel.age,
                onValueChange = { viewModel.age = it },
                label = { Text("Âge") }
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = viewModel.gender,
                onValueChange = { viewModel.gender = it },
                label = { Text("Sexe") }
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                navController.navigate("step2")
            }) {
                Text(text = "Suivant")
            }
        }
    }
    @Composable
    fun Step2Screen(navController: NavHostController, viewModel: RegisterViewModel) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Étape 2 : Coordonnées")
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = viewModel.city,
                onValueChange = { viewModel.city = it },
                label = { Text("Ville, Région") }
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = viewModel.email,
                onValueChange = { viewModel.email = it },
                label = { Text("Adresse email") }
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = viewModel.phoneNumber,
                onValueChange = { viewModel.phoneNumber = it },
                label = { Text("Numéro de téléphone") }
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                navController.navigate("step3")
            }) {
                Text(text = "Suivant")
            }
        }
    }

    @Composable
    fun Step3Screen(navController: NavHostController, viewModel: RegisterViewModel) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Étape 3 : Informations sportives")
            Spacer(modifier = Modifier.height(16.dp))

            // Types de sports pratiqués
            OutlinedTextField(
                value = viewModel.sports,
                onValueChange = { viewModel.sports = it },
                label = { Text("Types de sports pratiqués") }
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Niveau dans chaque sport
            OutlinedTextField(
                value = viewModel.sportLevels,
                onValueChange = { viewModel.sportLevels = it },
                label = { Text("Niveau dans chaque sport") }
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Type de partenaire recherché
            OutlinedTextField(
                value = viewModel.sportPartnerPreferences,
                onValueChange = { viewModel.sportPartnerPreferences = it },
                label = { Text("Type de partenaire recherché") }
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                navController.navigate("step4")
            }) {
                Text(text = "Suivant")
            }
        }
    }

    @Composable
    fun Step4Screen(navController: NavHostController, viewModel: RegisterViewModel) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Étape 4 : Objectifs et disponibilités")
            Spacer(modifier = Modifier.height(16.dp))

            // Objectifs sportifs
            OutlinedTextField(
                value = viewModel.sportGoals,
                onValueChange = { viewModel.sportGoals = it },
                label = { Text("Objectifs sportifs") }
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Fréquence des activités sportives
            OutlinedTextField(
                value = viewModel.activityFrequency,
                onValueChange = { viewModel.activityFrequency = it },
                label = { Text("Fréquence des activités sportives") }
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Plages horaires disponibles
            OutlinedTextField(
                value = viewModel.availableTimeSlots,
                onValueChange = { viewModel.availableTimeSlots = it },
                label = { Text("Plages horaires disponibles") }
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                navController.navigate("summary")
            }) {
                Text(text = "Suivant")
            }
        }
    }

    @Composable
    fun SummaryScreen(navController: NavHostController, viewModel: RegisterViewModel) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Résumé de l'inscription")
            Spacer(modifier = Modifier.height(16.dp))

            // Affichage du résumé des informations collectées
            Text(text = "Nom : ${viewModel.firstName} ${viewModel.lastName}")
            Text(text = "Pseudonyme : ${viewModel.pseudo}")
            Text(text = "Âge : ${viewModel.age}")
            Text(text = "Sexe : ${viewModel.gender}")
            Text(text = "Email : ${viewModel.email}")
            Text(text = "Numéro de téléphone : ${viewModel.phoneNumber}")
            Text(text = "Sports : ${viewModel.sports}")

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                // Logique pour enregistrer les informations
                navController.popBackStack() // Revenir à l'accueil ou terminer l'inscription
            }) {
                Text(text = "Terminer")
            }
        }
    }
