package com.example.serviceradar.data.model

data class BookingFilter(
    val status: String? = null, // pending, accepted, completed, rejected
    val startDate: Long? = null,
    val endDate: Long? = null,
    val minRating: Double = 0.0
)

data class ProviderFilter(
    val minPrice: Double = 0.0,
    val maxPrice: Double = 10000.0,
    val minRating: Double = 0.0,
    val searchQuery: String = "",
    val categories: List<String> = emptyList()
)

