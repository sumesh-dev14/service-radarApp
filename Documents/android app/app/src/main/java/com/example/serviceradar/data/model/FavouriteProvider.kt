package com.example.serviceradar.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite_providers")
data class FavouriteProvider(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val providerId: String,
    val category: String,
    val price: Double,
    val savedAt: Long = System.currentTimeMillis()
)

