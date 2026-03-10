package com.example.serviceradar.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LocationManager(private val context: Context) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    /**
     * Returns the device's current GPS coordinates as (latitude, longitude),
     * or null if permission is denied or location unavailable.
     *
     * First tries a fresh high-accuracy reading; falls back to last known location.
     */
    suspend fun getCurrentLocation(): Pair<Double, Double>? {
        if (!hasLocationPermission()) return null

        return try {
            getFreshLocation() ?: getLastKnownLocation()
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun getFreshLocation(): Pair<Double, Double>? =
        suspendCancellableCoroutine { cont ->
            if (!hasLocationPermission()) { cont.resume(null); return@suspendCancellableCoroutine }
            val cts = CancellationTokenSource()
            cont.invokeOnCancellation { cts.cancel() }
            fusedLocationClient
                .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
                .addOnSuccessListener { loc ->
                    cont.resume(if (loc != null) Pair(loc.latitude, loc.longitude) else null)
                }
                .addOnFailureListener { cont.resume(null) }
        }

    private suspend fun getLastKnownLocation(): Pair<Double, Double>? =
        suspendCancellableCoroutine { cont ->
            if (!hasLocationPermission()) { cont.resume(null); return@suspendCancellableCoroutine }
            fusedLocationClient.lastLocation
                .addOnSuccessListener { loc ->
                    cont.resume(if (loc != null) Pair(loc.latitude, loc.longitude) else null)
                }
                .addOnFailureListener { cont.resume(null) }
        }

    fun hasLocationPermission(): Boolean =
        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
}