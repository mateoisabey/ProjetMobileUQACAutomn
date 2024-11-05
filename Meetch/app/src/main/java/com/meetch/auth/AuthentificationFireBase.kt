package com.meetch.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class FirebaseAuthManager {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun signUpWithEmail(email: String, password: String, onComplete: (Boolean, FirebaseUser?, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, auth.currentUser, null)
                } else {
                    val errorMessage = task.exception?.message ?: "Erreur inconnue"
                    onComplete(false, null, errorMessage)
                }
            }
    }

    fun loginWithEmail(email: String, password: String, onComplete: (Boolean, FirebaseUser?, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, auth.currentUser, null)
                } else {
                    val errorMessage = task.exception?.message ?: "Erreur inconnue"
                    onComplete(false, null, errorMessage)
                }
            }
    }

    fun logout() {
        auth.signOut()
    }

    // Vérifier si un utilisateur est connecté
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    // Obtenir l'utilisateur actuel
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}