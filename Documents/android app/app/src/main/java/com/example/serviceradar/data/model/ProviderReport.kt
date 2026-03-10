package com.example.serviceradar.data.model

data class ProviderReport(
    val id: String = "",
    val providerId: String = "",
    val reporterId: String = "",
    val reason: String = "",
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "pending" // pending, reviewed, resolved, dismissed
)

