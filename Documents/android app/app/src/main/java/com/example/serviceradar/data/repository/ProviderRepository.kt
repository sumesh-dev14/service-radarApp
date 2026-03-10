package com.example.serviceradar.data.repository

import com.example.serviceradar.data.model.Booking
import com.example.serviceradar.data.model.Provider
import com.example.serviceradar.data.model.AnalyticsData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProviderRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun getCurrentUserId(): String? = auth.currentUser?.uid

    fun createOrUpdateProvider(
        category: String,
        price: Double,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val userId = getCurrentUserId()
        if (userId == null) {
            onError("User not authenticated")
            return
        }

        val providerData = mapOf(
            "userId" to userId,
            "category" to category,
            "price" to price,
            "isOnline" to false,
            "averageRating" to 0.0
        )

        firestore.collection("providers")
            .document(userId)
            .set(providerData)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Failed to create provider profile") }
    }

    fun toggleOnlineStatus(
        isOnline: Boolean,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val userId = getCurrentUserId()
        if (userId == null) {
            onError("User not authenticated")
            return
        }

        firestore.collection("providers")
            .document(userId)
            .update("isOnline", isOnline)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Failed to update online status") }
    }

    fun getProviderProfile(
        onResult: (Provider?) -> Unit
    ) {
        val userId = getCurrentUserId()
        if (userId == null) {
            onResult(null)
            return
        }

        firestore.collection("providers")
            .document(userId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val provider = Provider(
                        id = doc.id,
                        userId = doc.getString("userId") ?: "",
                        name = doc.getString("name") ?: "",
                        category = doc.getString("category") ?: "",
                        price = doc.getDouble("price") ?: 0.0,
                        isOnline = doc.getBoolean("isOnline") ?: false,
                        averageRating = doc.getDouble("averageRating") ?: 0.0
                    )
                    onResult(provider)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener { onResult(null) }
    }

    fun getIncomingBookings(
        onResult: (List<Booking>) -> Unit
    ) {
        val userId = getCurrentUserId()
        if (userId == null) {
            onResult(emptyList())
            return
        }

        firestore.collection("bookings")
            .whereEqualTo("providerId", userId)
            .whereIn("status", listOf("pending", "accepted"))
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }

                val bookings = snapshot?.documents?.map { doc ->
                    Booking(
                        id = doc.id,
                        customerId = doc.getString("customerId") ?: "",
                        providerId = doc.getString("providerId") ?: "",
                        serviceCategory = doc.getString("serviceCategory") ?: "",
                        status = doc.getString("status") ?: "pending",
                        timestamp = doc.getLong("timestamp") ?: 0L,
                        acceptedAt = doc.getLong("acceptedAt"),
                        completedAt = doc.getLong("completedAt")
                    )
                } ?: emptyList()
                onResult(bookings)
            }
    }

    fun getCompletedBookings(
        onResult: (List<Booking>) -> Unit
    ) {
        val userId = getCurrentUserId()
        if (userId == null) {
            onResult(emptyList())
            return
        }

        firestore.collection("bookings")
            .whereEqualTo("providerId", userId)
            .whereEqualTo("status", "completed")
            .orderBy("completedAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }

                val bookings = snapshot?.documents?.map { doc ->
                    Booking(
                        id = doc.id,
                        customerId = doc.getString("customerId") ?: "",
                        providerId = doc.getString("providerId") ?: "",
                        serviceCategory = doc.getString("serviceCategory") ?: "",
                        status = doc.getString("status") ?: "completed",
                        timestamp = doc.getLong("timestamp") ?: 0L,
                        acceptedAt = doc.getLong("acceptedAt"),
                        completedAt = doc.getLong("completedAt"),
                        price = doc.getDouble("price") ?: 0.0
                    )
                } ?: emptyList()
                onResult(bookings)
            }
    }

    fun updateBookingStatus(
        bookingId: String,
        status: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        firestore.collection("bookings")
            .document(bookingId)
            .update("status", status)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Error") }
    }

    fun getProviderAnalytics(
        providerId: String? = null,
        onResult: (AnalyticsData?) -> Unit
    ) {
        val userId = providerId ?: getCurrentUserId()
        if (userId == null) {
            onResult(null)
            return
        }

        firestore.collection("providers")
            .document(userId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val analytics = AnalyticsData(
                        providerId = userId,
                        totalBookings = doc.getLong("totalBookings")?.toInt() ?: 0,
                        completedBookings = doc.getLong("completedBookings")?.toInt() ?: 0,
                        totalEarnings = doc.getDouble("totalEarnings") ?: 0.0,
                        averageRating = doc.getDouble("averageRating") ?: 0.0,
                        ratingCount = doc.getLong("ratingCount")?.toInt() ?: 0,
                        weeklyEarnings = (doc.get("weeklyEarnings") as? List<*>)?.mapNotNull { (it as? Number)?.toDouble() }
                            ?: emptyList(),
                        monthlyEarnings = (doc.get("monthlyEarnings") as? List<*>)?.mapNotNull { (it as? Number)?.toDouble() }
                            ?: emptyList()
                    )
                    onResult(analytics)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener { onResult(null) }
    }

    fun getEarningsHistory(
        providerId: String? = null,
        onResult: (List<Pair<Long, Double>>) -> Unit
    ) {
        val userId = providerId ?: getCurrentUserId()
        if (userId == null) {
            onResult(emptyList())
            return
        }

        firestore.collection("earnings")
            .whereEqualTo("providerId", userId)
            .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(100)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }

                val earnings = snapshot?.documents?.mapNotNull { doc ->
                    val date = doc.getLong("date")
                    val amount = doc.getDouble("amount")
                    if (date != null && amount != null) {
                        Pair(date, amount)
                    } else null
                } ?: emptyList()
                onResult(earnings)
            }
    }

    fun updateProviderProfile(
        category: String,
        price: Double,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val userId = getCurrentUserId()
        if (userId == null) {
            onError("User not authenticated")
            return
        }

        val updates = mapOf(
            "category" to category,
            "price" to price
        )
        firestore.collection("providers")
            .document(userId)
            .update(updates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Failed to update profile") }
    }
}
