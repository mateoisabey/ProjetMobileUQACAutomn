package com.meetch.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun PostScreen(onActivityCreated: (ActivityData) -> Unit) {
    var currentStep by remember { mutableStateOf(0) }

    // Collecte des informations
    val activityName = remember { mutableStateOf(TextFieldValue("")) }
    val activityDate = remember { mutableStateOf(TextFieldValue("")) }
    val activityLocation = remember { mutableStateOf(TextFieldValue("")) }
    val activityDescription = remember { mutableStateOf(TextFieldValue("")) }

    // Firebase instances
    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (currentStep < 4) {  // Le 4 correspond à l'indice de la description dans les étapes
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
                    verticalArrangement = Arrangement.Center
                ) {
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

                    OutlinedTextField(
                        value = when (currentStep) {
                            0 -> activityName.value
                            1 -> activityDate.value
                            2 -> activityLocation.value
                            3 -> activityDescription.value
                            else -> TextFieldValue("")
                        },
                        onValueChange = { textFieldValue ->
                            when (currentStep) {
                                0 -> activityName.value = textFieldValue
                                1 -> activityDate.value = textFieldValue
                                2 -> activityLocation.value = textFieldValue
                                3 -> activityDescription.value = textFieldValue
                            }
                        },
                        label = { Text(when (currentStep) {
                            0 -> "Nom"
                            1 -> "Date"
                            2 -> "Lieu"
                            3 -> "Description"
                            else -> ""
                        }) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = if (currentStep == 1) KeyboardType.Number else KeyboardType.Text
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    IconButton(
                        onClick = {
                            if (when (currentStep) {
                                    0 -> activityName.value.text.isNotEmpty()
                                    1 -> activityDate.value.text.isNotEmpty()
                                    2 -> activityLocation.value.text.isNotEmpty()
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
        } else {
            Button(onClick = {
                if (currentUser != null) {
                    val activity = hashMapOf(
                        "userId" to currentUser.uid,
                        "name" to activityName.value.text,
                        "date" to activityDate.value.text,
                        "location" to activityLocation.value.text,
                        "description" to activityDescription.value.text,
                    )
                    db.collection("activities").add(activity)
                        .addOnSuccessListener {
                            onActivityCreated(
                                ActivityData(
                                    name = activityName.value.text,
                                    date = activityDate.value.text,
                                    location = activityLocation.value.text,
                                    description = activityDescription.value.text
                                )
                            )
                        }
                        .addOnFailureListener {
                            // Gérer l'échec de l'enregistrement
                        }
                }
            }) {
                Text("Enregistrer l'activité")
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