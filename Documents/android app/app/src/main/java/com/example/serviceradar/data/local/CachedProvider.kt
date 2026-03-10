package com.example.serviceradar.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_providers")
data class CachedProvider(
    @PrimaryKey
    val id: String,
    val userId: String,
    val category: String,
    val price: Double,
    val isOnline: Boolean,
    val averageRating: Double,

    // Location fields
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val serviceRadiusKm: Double = 10.0
)