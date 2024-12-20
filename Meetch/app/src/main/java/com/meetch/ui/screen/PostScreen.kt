package com.meetch.ui.screen

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen(onActivityCreated: (ActivityData) -> Unit, onCancel: () -> Unit) {
    // Gestion des étapes du formulaire
    var currentStep by remember { mutableStateOf(0) }

    // Champs de saisie pour les informations de l'activité
    val activityName = remember { mutableStateOf(TextFieldValue("")) }
    val activityDate = remember { mutableStateOf("") }
    val activityLocation = remember { mutableStateOf("") }
    val activityDescription = remember { mutableStateOf(TextFieldValue("")) }

    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val context = LocalContext.current

    // Options pour le menu déroulant des lieux
    val locationOptions = listOf("Stade", "Salle de gym", "Piscine", "Parc")
    var isDropdownExpanded by remember { mutableStateOf(false) }

    // Affichage du sélecteur de date
    fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                activityDate.value = "$dayOfMonth/${month + 1}/$year"
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (currentStep < 4) {
            // Formulaire pas à pas
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .aspectRatio(1f)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Titre indiquant l'étape actuelle
                    Text(
                        text = when (currentStep) {
                            0 -> "Nom de l'activité"
                            1 -> "Date de l'activité (JJ/MM/AAAA)"
                            2 -> "Lieu de l'activité"
                            3 -> "Description de l'activité"
                            else -> ""
                        },
                        style = MaterialTheme.typography.titleSmall,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Champs de saisie selon l'étape
                    when (currentStep) {
                        0 -> {
                            OutlinedTextField(
                                value = activityName.value,
                                onValueChange = { activityName.value = it },
                                label = { Text("Nom") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        1 -> {
                            Button(onClick = { showDatePickerDialog() }) {
                                Text(
                                    text = if (activityDate.value.isEmpty()) "Sélectionnez la date" else activityDate.value
                                )
                            }
                        }
                        2 -> {
                            ExposedDropdownMenuBox(
                                expanded = isDropdownExpanded,
                                onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
                            ) {
                                OutlinedTextField(
                                    value = activityLocation.value,
                                    onValueChange = { },
                                    label = { Text("Lieu") },
                                    readOnly = true,
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor()
                                )

                                ExposedDropdownMenu(
                                    expanded = isDropdownExpanded,
                                    onDismissRequest = { isDropdownExpanded = false }
                                ) {
                                    locationOptions.forEach { location ->
                                        DropdownMenuItem(
                                            text = { Text(location) },
                                            onClick = {
                                                activityLocation.value = location
                                                isDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        3 -> {
                            OutlinedTextField(
                                value = activityDescription.value,
                                onValueChange = { activityDescription.value = it },
                                label = { Text("Description") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Boutons "Retour" et "Suivant/Annuler"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (currentStep > 0) {
                            Button(onClick = { currentStep-- }) {
                                Text("Retour")
                            }
                        } else {
                            Button(onClick = { onCancel() }) {
                                Text("Annuler")
                            }
                        }

                        IconButton(
                            onClick = {
                                if (when (currentStep) {
                                        0 -> activityName.value.text.isNotEmpty()
                                        1 -> activityDate.value.isNotEmpty()
                                        2 -> activityLocation.value.isNotEmpty()
                                        3 -> activityDescription.value.text.isNotEmpty()
                                        else -> false
                                    }) {
                                    currentStep++
                                }
                            }
                        ) {
                            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Suivant")
                        }
                    }
                }
            }
        } else {
            // Résumé des informations saisies
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Résumé de l'activité",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Nom : ${activityName.value.text}")
                Text("Date : ${activityDate.value}")
                Text("Lieu : ${activityLocation.value}")
                Text("Description : ${activityDescription.value.text}")

                Spacer(modifier = Modifier.height(16.dp))

                // Boutons "Modifier" et "Enregistrer"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = { currentStep-- }) {
                        Text("Modifier")
                    }
                    Button(onClick = {
                        if (currentUser != null) {
                            val activity = hashMapOf(
                                "userId" to currentUser.uid,
                                "name" to activityName.value.text,
                                "date" to activityDate.value,
                                "location" to activityLocation.value,
                                "description" to activityDescription.value.text,
                            )
                            db.collection("activities").add(activity)
                                .addOnSuccessListener {
                                    onActivityCreated(
                                        ActivityData(
                                            name = activityName.value.text,
                                            date = activityDate.value,
                                            location = activityLocation.value,
                                            description = activityDescription.value.text
                                        )
                                    )
                                }
                                .addOnFailureListener {
                                    // Gérer l'échec de l'enregistrement
                                }
                        }
                    }) {
                        Text("Enregistrer")
                    }
                }
            }
        }
    }
}


data class ActivityData(
    val name: String,
    val date: String,
    val location: String,
    val description: String
)