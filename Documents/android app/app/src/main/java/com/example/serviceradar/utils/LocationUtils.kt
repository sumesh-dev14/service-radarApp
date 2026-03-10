package com.example.serviceradar.utils

import kotlin.math.*

object LocationUtils {

    /**
     * Haversine formula — calculates the distance between two GPS coordinates in kilometres.
     */
    fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val R = 6371.0 // Earth radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }

    /**
     * Human-readable distance label: "800 m away" or "2.3 km away"
     */
    fun formatDistance(distanceKm: Double): String {
        return if (distanceKm < 1.0)
            "${(distanceKm * 1000).toInt()} m away"
        else
            "${"%.1f".format(distanceKm)} km away"
    }

    /**
     * Checks if a provider's coordinates are valid (not default 0,0)
     */
    fun hasValidLocation(lat: Double, lon: Double): Boolean {
        return lat != 0.0 && lon != 0.0
    }
}