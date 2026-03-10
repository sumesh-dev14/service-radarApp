package com.example.serviceradar.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_bookings")
data class CachedBooking(
    @PrimaryKey
    val id: String,
    val customerId: String,
    val providerId: String,
    val serviceCategory: String,
    val status: String,
    val timestamp: Long
)