package com.example.serviceradar.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_analytics")
data class CachedAnalytics(
    @PrimaryKey
    val providerId: String,
    val totalBookings: Int,
    val completedBookings: Int,
    val totalEarnings: Double,
    val averageRating: Double,
    val ratingCount: Int,
    val weeklyEarningsJson: String, // JSON string of List<Double>
    val monthlyEarningsJson: String, // JSON string of List<Double>
    val lastUpdated: Long
)

