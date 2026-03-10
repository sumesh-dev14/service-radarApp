package com.example.serviceradar.data.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "",

    // Location fields
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)