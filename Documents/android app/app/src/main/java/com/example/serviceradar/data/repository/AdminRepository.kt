package com.example.serviceradar.data.repository

import com.example.serviceradar.data.model.Booking
import com.example.serviceradar.data.model.Provider
import com.example.serviceradar.data.model.User
import com.google.firebase.firestore.FirebaseFirestore

class AdminRepository {

    private val firestore = FirebaseFirestore.getInstance()

    fun getAllUsers(onResult: (List<User>) -> Unit) {
        firestore.collection("users")
            .get()
            .addOnSuccessListener { snapshot ->
                val users = snapshot.documents.map { doc ->
                    User(
                        id = doc.id,
                        email = doc.getString("email") ?: "",
                        role = doc.getString("role") ?: ""
                    )
                }
                onResult(users)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun getAllProviders(onResult: (List<Provider>) -> Unit) {
        firestore.collection("providers")
            .get()
            .addOnSuccessListener { snapshot ->
                val providers = snapshot.documents.map { doc ->
                    Provider(
                        id = doc.id,
                        userId = doc.getString("userId") ?: "",
                        category = doc.getString("category") ?: "",
                        price = doc.getDouble("price") ?: 0.0,
                        isOnline = doc.getBoolean("isOnline") ?: false,
                        averageRating = doc.getDouble("averageRating") ?: 0.0
                    )
                }
                onResult(providers)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun getAllBookings(onResult: (List<Booking>) -> Unit) {
        firestore.collection("bookings")
            .get()
            .addOnSuccessListener { snapshot ->
                val bookings = snapshot.documents.map { doc ->
                    Booking(
                        id = doc.id,
                        customerId = doc.getString("customerId") ?: "",
                        providerId = doc.getString("providerId") ?: "",
                        serviceCategory = doc.getString("serviceCategory") ?: "",
                        status = doc.getString("status") ?: "",
                        timestamp = doc.getLong("timestamp") ?: 0L
                    )
                }
                onResult(bookings)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun removeProvider(
        providerId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        firestore.collection("providers")
            .document(providerId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Error") }
    }
}