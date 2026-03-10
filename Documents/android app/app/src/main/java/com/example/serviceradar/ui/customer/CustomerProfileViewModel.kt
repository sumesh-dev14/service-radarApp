package com.example.serviceradar.ui.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CustomerProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _displayName = MutableStateFlow("")
    val displayName: StateFlow<String> = _displayName

    private val _totalBookings = MutableStateFlow(0)
    val totalBookings: StateFlow<Int> = _totalBookings

    private val _completedBookings = MutableStateFlow(0)
    val completedBookings: StateFlow<Int> = _completedBookings

    private val _pendingBookings = MutableStateFlow(0)
    val pendingBookings: StateFlow<Int> = _pendingBookings

    private val _cancelledBookings = MutableStateFlow(0)
    val cancelledBookings: StateFlow<Int> = _cancelledBookings

    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage

    init {
        loadProfile()
        loadBookingStats()
    }

    fun loadProfile() {
        val user = auth.currentUser
        _email.value = user?.email ?: ""
        _displayName.value = user?.displayName ?: ""
    }

    fun updateDisplayName(newName: String) {
        val user = auth.currentUser ?: return
        val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(newName).build()
        user.updateProfile(profileUpdates)
            .addOnSuccessListener {
                _displayName.value = newName
                _uiMessage.value = "Display name updated"
            }
            .addOnFailureListener {
                _uiMessage.value = it.message ?: "Failed to update name"
            }
    }

    fun loadBookingStats() {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("bookings")
            .whereEqualTo("customerId", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                val bookings = snapshot.documents
                _totalBookings.value = bookings.size
                _completedBookings.value = bookings.count { it.getString("status") == "completed" }
                _pendingBookings.value = bookings.count { it.getString("status") == "pending" }
                _cancelledBookings.value = bookings.count { it.getString("status") == "cancelled" }
            }
    }

    fun clearMessage() {
        _uiMessage.value = null
    }
}
