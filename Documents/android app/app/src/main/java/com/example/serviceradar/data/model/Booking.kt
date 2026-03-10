package com.example.serviceradar.data.model

data class Booking(
    val id: String = "",
    val customerId: String = "",
    val providerId: String = "",
    val serviceCategory: String = "",
    val status: String = "pending", // pending, accepted, completed, rejected
    val timestamp: Long = 0L,
    val rating: Double = 0.0,
    val isRated: Boolean = false,
    val acceptedAt: Long? = null,
    val completedAt: Long? = null,
    val rejectedAt: Long? = null,
    val price: Double = 0.0,
    val review: String? = null,
    val scheduledDate: String = "",   // e.g. "2026-03-15"
    val scheduledTime: String = ""    // e.g. "10:30 AM"
)

data class BookingStatusTimeline(
    val step: String,
    val timestamp: Long? = null,
    val isCompleted: Boolean = false
)