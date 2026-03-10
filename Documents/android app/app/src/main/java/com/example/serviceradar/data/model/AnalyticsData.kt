package com.example.serviceradar.data.model

data class AnalyticsData(
    val providerId: String = "",
    val totalBookings: Int = 0,
    val completedBookings: Int = 0,
    val totalEarnings: Double = 0.0,
    val averageRating: Double = 0.0,
    val ratingCount: Int = 0,
    val weeklyEarnings: List<Double> = emptyList(), // Last 7 days
    val monthlyEarnings: List<Double> = emptyList(), // Last 30 days
    val lastUpdated: Long = System.currentTimeMillis()
)

