package com.example.serviceradar.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "provider_reports")
data class CachedProviderReport(
    @PrimaryKey
    val id: String,
    val providerId: String,
    val reporterId: String,
    val reason: String,
    val description: String,
    val timestamp: Long,
    val status: String
)

