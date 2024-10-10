package com.meetch.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class RegisterViewModel : ViewModel() {
    // Informations personnelles
    var firstName by mutableStateOf("")
    var lastName by mutableStateOf("")
    var pseudo by mutableStateOf("")
    var age by mutableStateOf("")
    var gender by mutableStateOf("")

    // Coordonnées
    var city by mutableStateOf("")
    var email by mutableStateOf("")
    var phoneNumber by mutableStateOf("")

    // Informations sportives
    var sports by mutableStateOf("")
    var sportLevels by mutableStateOf("")
    var sportPartnerPreferences by mutableStateOf("")

    // Objectifs sportifs et disponibilités
    var sportGoals by mutableStateOf("")
    var activityFrequency by mutableStateOf("")
    var availableTimeSlots by mutableStateOf("")
}