package com.example.serviceradar.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun checkCurrentUser(): String? {
        return auth.currentUser?.uid
    }

    fun fetchRoleForCurrentUser(onResult: (String?) -> Unit) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            onResult(null)
            return
        }
        firestore.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                val role = document.getString("role")
                onResult(role)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    fun login(email: String, password: String) {
        _authState.value = AuthState.Loading

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                    fetchUserRole(userId)
                } else {
                    _authState.value = AuthState.Error(
                        task.exception?.message ?: "Login failed"
                    )
                }
            }
    }

    private fun fetchUserRole(userId: String) {
        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                val role = document.getString("role") ?: "Customer"
                _authState.value = AuthState.RoleSuccess(role)
            }
            .addOnFailureListener {
                _authState.value = AuthState.Error("Failed to fetch role")
            }
    }

    fun register(email: String, password: String, role: String, name: String = "") {
        _authState.value = AuthState.Loading

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: return@addOnCompleteListener

                    val userMap = mapOf(
                        "email" to email,
                        "role" to role,
                        "name" to name
                    )

                    firestore.collection("users")
                        .document(userId)
                        .set(userMap)
                        .addOnSuccessListener {
                            _authState.value = AuthState.RoleSuccess(role)
                        }
                        .addOnFailureListener {
                            _authState.value = AuthState.Error("Failed to save role")
                        }
                } else {
                    _authState.value = AuthState.Error(
                        task.exception?.message ?: "Registration failed"
                    )
                }
            }
    }
    fun logout() {
        auth.signOut()
        _authState.value = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class RoleSuccess(val role: String) : AuthState()
    data class Error(val message: String) : AuthState()
}