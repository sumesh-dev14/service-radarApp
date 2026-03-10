package com.example.serviceradar.data.repository

import android.content.Context
import com.example.serviceradar.data.local.AppDatabase
import com.example.serviceradar.data.local.CachedBooking
import com.example.serviceradar.data.local.CachedProvider
import com.example.serviceradar.data.model.Booking
import com.example.serviceradar.data.model.Provider
import com.example.serviceradar.data.model.BookingFilter
import com.example.serviceradar.data.model.ProviderFilter
import com.example.serviceradar.data.model.ProviderReport
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CustomerRepository(context: Context) {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val dao = AppDatabase.getInstance(context).serviceRadarDao()

    fun getCurrentUserId(): String? = auth.currentUser?.uid

    fun getProvidersByCategory(
        category: String,
        isOnline: Boolean,
        onResult: (List<Provider>) -> Unit
    ) {
        if (isOnline) {
            firestore.collection("providers")
                .whereEqualTo("category", category)
                .whereEqualTo("isOnline", true)
                .get()
                .addOnSuccessListener { snapshot ->
                    val docs = snapshot.documents
                    if (docs.isEmpty()) { onResult(emptyList()); return@addOnSuccessListener }

                    val result = mutableListOf<Provider>()
                    var completed = 0

                    docs.forEach { doc ->
                        val userId = doc.getString("userId") ?: ""
                        val partialProvider = Provider(
                            id = doc.id,
                            userId = userId,
                            category = doc.getString("category") ?: "",
                            price = doc.getDouble("price") ?: 0.0,
                            isOnline = doc.getBoolean("isOnline") ?: false,
                            averageRating = doc.getDouble("averageRating") ?: 0.0,
                            latitude = doc.getDouble("latitude") ?: 0.0,
                            longitude = doc.getDouble("longitude") ?: 0.0,
                            serviceRadiusKm = doc.getDouble("serviceRadiusKm") ?: 10.0
                        )
                        if (userId.isEmpty()) {
                            result.add(partialProvider)
                            completed++
                            if (completed == docs.size) onResult(result)
                        } else {
                            firestore.collection("users").document(userId)
                                .get()
                                .addOnSuccessListener { userDoc ->
                                    result.add(partialProvider.copy(name = userDoc.getString("name") ?: ""))
                                    completed++
                                    if (completed == docs.size) {
                                        CoroutineScope(Dispatchers.IO).launch {
                                            dao.clearProviders()
                                            dao.insertProviders(result.map {
                                                CachedProvider(
                                                    id = it.id,
                                                    userId = it.userId,
                                                    category = it.category,
                                                    price = it.price,
                                                    isOnline = it.isOnline,
                                                    averageRating = it.averageRating,
                                                    latitude = it.latitude,
                                                    longitude = it.longitude,
                                                    serviceRadiusKm = it.serviceRadiusKm
                                                )
                                            })
                                        }
                                        onResult(result)
                                    }
                                }
                                .addOnFailureListener {
                                    result.add(partialProvider)
                                    completed++
                                    if (completed == docs.size) onResult(result)
                                }
                        }
                    }
                }
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                val cached = dao.getProvidersByCategory(category)
                val providers = cached.map {
                    Provider(
                        id = it.id,
                        userId = it.userId,
                        category = it.category,
                        price = it.price,
                        isOnline = it.isOnline,
                        averageRating = it.averageRating,
                        latitude = it.latitude,
                        longitude = it.longitude,
                        serviceRadiusKm = it.serviceRadiusKm
                    )
                }
                onResult(providers)
            }
        }
    }

    // ── FIXED: now saves scheduledDate + scheduledTime to Firestore ────────────
    fun createBooking(
        providerId: String,
        serviceCategory: String,
        scheduledDate: String = "",
        scheduledTime: String = "",
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val customerId = getCurrentUserId()
        if (customerId == null) {
            onError("User not authenticated")
            return
        }

        val booking = mapOf(
            "customerId" to customerId,
            "providerId" to providerId,
            "serviceCategory" to serviceCategory,
            "scheduledDate" to scheduledDate,
            "scheduledTime" to scheduledTime,
            "status" to "pending",
            "timestamp" to System.currentTimeMillis()
        )

        firestore.collection("bookings")
            .add(booking)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Failed to create booking") }
    }

    fun getMyBookings(
        isOnline: Boolean,
        onResult: (List<Booking>) -> Unit
    ) {
        val customerId = getCurrentUserId()
        if (customerId == null) { onResult(emptyList()); return }

        if (isOnline) {
            firestore.collection("bookings")
                .whereEqualTo("customerId", customerId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) { onResult(emptyList()); return@addSnapshotListener }

                    val bookings = snapshot?.documents?.map { doc ->
                        Booking(
                            id = doc.id,
                            customerId = doc.getString("customerId") ?: "",
                            providerId = doc.getString("providerId") ?: "",
                            serviceCategory = doc.getString("serviceCategory") ?: "",
                            status = doc.getString("status") ?: "pending",
                            timestamp = doc.getLong("timestamp") ?: 0L,
                            rating = doc.getDouble("rating") ?: 0.0,
                            isRated = doc.getBoolean("isRated") ?: false,
                            acceptedAt = doc.getLong("acceptedAt"),
                            completedAt = doc.getLong("completedAt"),
                            rejectedAt = doc.getLong("rejectedAt"),
                            price = doc.getDouble("price") ?: 0.0,
                            review = doc.getString("review"),
                            scheduledDate = doc.getString("scheduledDate") ?: "",
                            scheduledTime = doc.getString("scheduledTime") ?: ""
                        )
                    } ?: emptyList()

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            dao.clearBookings()
                            dao.insertBookings(bookings.map {
                                CachedBooking(
                                    id = it.id,
                                    customerId = it.customerId,
                                    providerId = it.providerId,
                                    serviceCategory = it.serviceCategory,
                                    status = it.status,
                                    timestamp = it.timestamp
                                )
                            })
                        } catch (e: Exception) { }
                    }
                    onResult(bookings)
                }
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val cached = dao.getBookingsByCustomer(customerId)
                    val bookings = cached.map {
                        Booking(
                            id = it.id,
                            customerId = it.customerId,
                            providerId = it.providerId,
                            serviceCategory = it.serviceCategory,
                            status = it.status,
                            timestamp = it.timestamp
                        )
                    }
                    onResult(bookings)
                } catch (e: Exception) { onResult(emptyList()) }
            }
        }
    }

    fun submitRating(
        bookingId: String,
        providerId: String,
        rating: Double,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        firestore.collection("bookings").document(bookingId)
            .update(mapOf("rating" to rating, "isRated" to true))
            .addOnSuccessListener { updateProviderRating(providerId, rating, onSuccess, onError) }
            .addOnFailureListener { onError(it.message ?: "Error") }
    }

    private fun updateProviderRating(
        providerId: String,
        newRating: Double,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        firestore.collection("providers").document(providerId).get()
            .addOnSuccessListener { doc ->
                val currentRating = doc.getDouble("averageRating") ?: 0.0
                val updatedRating = if (currentRating == 0.0) newRating else (currentRating + newRating) / 2
                firestore.collection("providers").document(providerId)
                    .update("averageRating", updatedRating)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onError(it.message ?: "Error") }
            }
            .addOnFailureListener { onError(it.message ?: "Error") }
    }

    fun searchProviders(
        query: String,
        isOnline: Boolean,
        onResult: (List<Provider>) -> Unit
    ) {
        if (!isOnline) { onResult(emptyList()); return }
        firestore.collection("providers").get()
            .addOnSuccessListener { snapshot ->
                val providers = snapshot.documents.mapNotNull { doc ->
                    val category = doc.getString("category") ?: ""
                    if (category.contains(query, ignoreCase = true) ||
                        doc.getString("name")?.contains(query, ignoreCase = true) == true) {
                        Provider(
                            id = doc.id,
                            userId = doc.getString("userId") ?: "",
                            name = doc.getString("name") ?: "",
                            category = category,
                            price = doc.getDouble("price") ?: 0.0,
                            isOnline = doc.getBoolean("isOnline") ?: false,
                            averageRating = doc.getDouble("averageRating") ?: 0.0,
                            totalBookings = doc.getLong("totalBookings")?.toInt() ?: 0,
                            completedBookings = doc.getLong("completedBookings")?.toInt() ?: 0,
                            totalEarnings = doc.getDouble("totalEarnings") ?: 0.0,
                            ratingCount = doc.getLong("ratingCount")?.toInt() ?: 0,
                            reportsCount = doc.getLong("reportsCount")?.toInt() ?: 0,
                            description = doc.getString("description") ?: "",
                            imageUrl = doc.getString("imageUrl") ?: "",
                            latitude = doc.getDouble("latitude") ?: 0.0,
                            longitude = doc.getDouble("longitude") ?: 0.0,
                            serviceRadiusKm = doc.getDouble("serviceRadiusKm") ?: 10.0
                        )
                    } else null
                }
                onResult(providers)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun filterProviders(
        filter: ProviderFilter,
        isOnline: Boolean,
        onResult: (List<Provider>) -> Unit
    ) {
        if (isOnline) {
            firestore.collection("providers").get()
                .addOnSuccessListener { snapshot ->
                    val providers = snapshot.documents.mapNotNull { doc ->
                        val price = doc.getDouble("price") ?: 0.0
                        val rating = doc.getDouble("averageRating") ?: 0.0
                        val category = doc.getString("category") ?: ""
                        val matchesPrice = price in filter.minPrice..filter.maxPrice
                        val matchesRating = rating >= filter.minRating
                        val matchesCategory = filter.categories.isEmpty() || category in filter.categories
                        val matchesSearch = filter.searchQuery.isEmpty() || category.contains(filter.searchQuery, ignoreCase = true)
                        if (matchesPrice && matchesRating && matchesCategory && matchesSearch) {
                            Provider(
                                id = doc.id,
                                userId = doc.getString("userId") ?: "",
                                category = category,
                                price = price,
                                isOnline = doc.getBoolean("isOnline") ?: false,
                                averageRating = rating,
                                totalBookings = doc.getLong("totalBookings")?.toInt() ?: 0,
                                completedBookings = doc.getLong("completedBookings")?.toInt() ?: 0,
                                totalEarnings = doc.getDouble("totalEarnings") ?: 0.0,
                                ratingCount = doc.getLong("ratingCount")?.toInt() ?: 0,
                                reportsCount = doc.getLong("reportsCount")?.toInt() ?: 0,
                                description = doc.getString("description") ?: "",
                                imageUrl = doc.getString("imageUrl") ?: "",
                                latitude = doc.getDouble("latitude") ?: 0.0,
                                longitude = doc.getDouble("longitude") ?: 0.0,
                                serviceRadiusKm = doc.getDouble("serviceRadiusKm") ?: 10.0
                            )
                        } else null
                    }
                    onResult(providers)
                }
                .addOnFailureListener { onResult(emptyList()) }
        }
    }

    fun filterBookingHistory(
        filter: BookingFilter,
        isOnline: Boolean,
        onResult: (List<Booking>) -> Unit
    ) {
        val customerId = getCurrentUserId()
        if (customerId == null) { onResult(emptyList()); return }
        if (isOnline) {
            firestore.collection("bookings")
                .whereEqualTo("customerId", customerId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) { onResult(emptyList()); return@addSnapshotListener }
                    val bookings = snapshot?.documents?.mapNotNull { doc ->
                        val status = doc.getString("status") ?: "pending"
                        val timestamp = doc.getLong("timestamp") ?: 0L
                        val matchesStatus = filter.status == null || status == filter.status
                        val matchesStartDate = filter.startDate == null || timestamp >= filter.startDate
                        val matchesEndDate = filter.endDate == null || timestamp <= filter.endDate
                        if (matchesStatus && matchesStartDate && matchesEndDate) {
                            Booking(
                                id = doc.id,
                                customerId = doc.getString("customerId") ?: "",
                                providerId = doc.getString("providerId") ?: "",
                                serviceCategory = doc.getString("serviceCategory") ?: "",
                                status = status,
                                timestamp = timestamp,
                                rating = doc.getDouble("rating") ?: 0.0,
                                isRated = doc.getBoolean("isRated") ?: false,
                                acceptedAt = doc.getLong("acceptedAt"),
                                completedAt = doc.getLong("completedAt"),
                                rejectedAt = doc.getLong("rejectedAt"),
                                price = doc.getDouble("price") ?: 0.0
                            )
                        } else null
                    } ?: emptyList()
                    onResult(bookings)
                }
        } else {
            onResult(emptyList())
        }
    }

    fun reportProvider(
        providerId: String,
        reason: String,
        description: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val reporterId = getCurrentUserId()
        if (reporterId == null) { onError("User not authenticated"); return }
        val report = mapOf(
            "providerId" to providerId,
            "reporterId" to reporterId,
            "reason" to reason,
            "description" to description,
            "timestamp" to System.currentTimeMillis(),
            "status" to "pending"
        )
        firestore.collection("provider_reports").add(report)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Failed to report provider") }
    }

    fun submitRatingAndReview(
        bookingId: String,
        providerId: String,
        rating: Double,
        review: String,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        val userId = getCurrentUserId() ?: return onError()
        val bookingRef = firestore.collection("bookings").document(bookingId)
        bookingRef.update(mapOf("rating" to rating, "isRated" to true, "review" to review))
            .addOnSuccessListener {
                val providerRef = firestore.collection("providers").document(providerId)
                providerRef.get().addOnSuccessListener { doc ->
                    val currentRating = doc.getDouble("averageRating") ?: 0.0
                    val ratingCount = doc.getLong("ratingCount") ?: 0L
                    val newCount = ratingCount + 1
                    val newRating = ((currentRating * ratingCount) + rating) / newCount
                    providerRef.update(mapOf("averageRating" to newRating, "ratingCount" to newCount))
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { onError() }
                }.addOnFailureListener { onError() }
            }.addOnFailureListener { onError() }
    }

    fun cancelBooking(
        bookingId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        firestore.collection("bookings").document(bookingId)
            .update("status", "cancelled")
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Error") }
    }

    fun saveCustomerLocation(
        lat: Double,
        lon: Double,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val uid = getCurrentUserId()
        if (uid == null) { onError("Not authenticated"); return }
        firestore.collection("users").document(uid)
            .update(mapOf("latitude" to lat, "longitude" to lon))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Error saving location") }
    }
}