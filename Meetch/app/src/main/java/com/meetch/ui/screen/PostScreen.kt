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

@Composable
fun PostScreen(onActivityCreated: (ActivityData) -> Unit) {
    var currentStep by remember { mutableStateOf(0) }

    // Collecte des informations
    val activityName = remember { mutableStateOf(TextFieldValue("")) }
    val activityDate = remember { mutableStateOf(TextFieldValue("")) }
    val activityLocation = remember { mutableStateOf(TextFieldValue("")) }
    val activityDescription = remember { mutableStateOf(TextFieldValue("")) }

    val steps = listOf(
        "Nom de l'activité" to activityName,
        "Date de l'activité (JJ/MM/AAAA)" to activityDate,
        "Lieu de l'activité" to activityLocation,
        "Description de l'activité" to activityDescription
    )

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (currentStep < steps.size) {
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
                        text = steps[currentStep].first,
                        style = MaterialTheme.typography.titleSmall,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = steps[currentStep].second.value,
                        onValueChange = { steps[currentStep].second.value = it },
                        label = { Text(steps[currentStep].first) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = when (currentStep) {
                                1 -> KeyboardType.Number // For Date
                                else -> KeyboardType.Text
                            }
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    IconButton(
                        onClick = {
                            if (steps[currentStep].second.value.text.isNotEmpty()) {
                                currentStep++
                            }
                        }
                    ) {
                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Suivant")
                    }
                }
            }
        } else {
            // Une fois tous les champs remplis, appeler la fonction de création d'activité
            onActivityCreated(
                ActivityData(
                    name = activityName.value.text,
                    date = activityDate.value.text,
                    location = activityLocation.value.text,
                    description = activityDescription.value.text
                )
            )
            Text(
                text = "Activité créée avec succès !",
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center
            )
        }
    }
}

data class ActivityData(
    val name: String,
    val date: String,
    val location: String,
    val description: String
)