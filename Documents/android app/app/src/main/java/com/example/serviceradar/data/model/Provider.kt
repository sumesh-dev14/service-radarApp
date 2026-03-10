package com.example.serviceradar.data.model

data class Provider(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val category: String = "",
    val price: Double = 0.0,
    val isOnline: Boolean = false,
    val averageRating: Double = 0.0,
    val totalBookings: Int = 0,
    val completedBookings: Int = 0,
    val totalEarnings: Double = 0.0,
    val ratingCount: Int = 0,
    val reportsCount: Int = 0,
    val description: String = "",
    val imageUrl: String = "",

    // Location fields
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val serviceRadiusKm: Double = 10.0,
    val distanceKm: Double? = null
)