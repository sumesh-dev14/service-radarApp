package com.example.serviceradar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.serviceradar.data.model.AnalyticsData
import com.example.serviceradar.data.model.Booking
import com.example.serviceradar.data.model.Provider
import com.example.serviceradar.data.repository.ProviderRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProviderViewModel : ViewModel() {

    private val repository = ProviderRepository()
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // ── Existing state flows (unchanged) ──────────────────────────────────────

    private val _providerProfile = MutableStateFlow<Provider?>(null)
    val providerProfile: StateFlow<Provider?> = _providerProfile

    private val _incomingBookings = MutableStateFlow<List<Booking>>(emptyList())
    val incomingBookings: StateFlow<List<Booking>> = _incomingBookings

    private val _isOnline = MutableStateFlow(false)
    val isOnline: StateFlow<Boolean> = _isOnline

    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _analyticsData = MutableStateFlow<AnalyticsData?>(null)
    val analyticsData: StateFlow<AnalyticsData?> = _analyticsData

    private val _earningsHistory = MutableStateFlow<List<Pair<Long, Double>>>(emptyList())
    val earningsHistory: StateFlow<List<Pair<Long, Double>>> = _earningsHistory

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    // ── NEW: Location state flow ───────────────────────────────────────────────

    private val _providerLocation = MutableStateFlow<Pair<Double, Double>?>(null)
    val providerLocation: StateFlow<Pair<Double, Double>?> = _providerLocation

    private val _displayName = MutableStateFlow("")
    val displayName: StateFlow<String> = _displayName

    // ─────────────────────────────────────────────────────────────────────────

    init {
        loadProviderProfile()
        loadIncomingBookings()
        loadProviderName()
    }

    // ── Existing functions (unchanged) ────────────────────────────────────────

    fun loadProviderProfile() {
        repository.getProviderProfile { provider ->
            _providerProfile.value = provider
            _isOnline.value = provider?.isOnline ?: false
        }
    }

    fun loadIncomingBookings() {
        repository.getIncomingBookings { bookings ->
            _incomingBookings.value = bookings
        }
    }

    fun toggleOnlineStatus() {
        val newStatus = !_isOnline.value
        _isLoading.value = true
        repository.toggleOnlineStatus(
            isOnline = newStatus,
            onSuccess = {
                _isOnline.value = newStatus
                _isLoading.value = false
                _uiMessage.value = if (newStatus) "You are now Online" else "You are now Offline"
            },
            onError = {
                _isLoading.value = false
                _uiMessage.value = "Failed to update status"
            }
        )
    }

    fun createOrUpdateProvider(category: String, price: Double) {
        _isLoading.value = true
        repository.createOrUpdateProvider(
            category = category,
            price = price,
            onSuccess = {
                _isLoading.value = false
                _uiMessage.value = "Profile saved successfully"
                loadProviderProfile()
            },
            onError = {
                _isLoading.value = false
                _uiMessage.value = "Failed to save profile"
            }
        )
    }

    fun acceptBooking(bookingId: String) {
        repository.updateBookingStatus(
            bookingId = bookingId,
            status = "accepted",
            onSuccess = {
                _uiMessage.value = "Booking accepted"
                loadIncomingBookings()
            },
            onError = { _uiMessage.value = "Failed to accept booking" }
        )
    }

    fun rejectBooking(bookingId: String) {
        repository.updateBookingStatus(
            bookingId = bookingId,
            status = "rejected",
            onSuccess = {
                _uiMessage.value = "Booking rejected"
                loadIncomingBookings()
            },
            onError = { _uiMessage.value = "Failed to reject booking" }
        )
    }

    fun completeBooking(bookingId: String) {
        repository.updateBookingStatus(
            bookingId = bookingId,
            status = "completed",
            onSuccess = {
                _uiMessage.value = "Booking marked as completed"
                loadIncomingBookings()
            },
            onError = { _uiMessage.value = "Failed to complete booking" }
        )
    }

    fun clearMessage() {
        _uiMessage.value = null
    }

    fun loadAnalytics() {
        val providerId = _providerProfile.value?.id ?: return
        repository.getProviderAnalytics(providerId) { analytics ->
            _analyticsData.value = analytics
        }
    }

    fun loadEarningsHistory() {
        val providerId = _providerProfile.value?.id ?: return
        repository.getEarningsHistory(providerId) { earnings ->
            _earningsHistory.value = earnings
        }
    }

    fun compareProviders(otherProvider: Provider): Boolean {
        return (_providerProfile.value?.averageRating ?: 0.0) >= otherProvider.averageRating &&
                (_providerProfile.value?.completedBookings ?: 0) >= otherProvider.completedBookings
    }

    fun refreshDashboard() {
        _isRefreshing.value = true
        loadProviderProfile()
        loadIncomingBookings()
        loadAnalytics()
        loadEarningsHistory()
        _isRefreshing.value = false
    }

    fun updateProfile(category: String, price: Double) {
        _isLoading.value = true
        repository.updateProviderProfile(
            category = category,
            price = price,
            onSuccess = {
                _isLoading.value = false
                _uiMessage.value = "Profile updated successfully"
                loadProviderProfile()
            },
            onError = {
                _isLoading.value = false
                _uiMessage.value = it
            }
        )
    }

    // ── NEW: Location functions ────────────────────────────────────────────────

    fun saveProviderLocation(lat: Double, lon: Double) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                firestore.collection("providers")
                    .document(uid)
                    .update(mapOf("latitude" to lat, "longitude" to lon))
                    .await()
                _providerLocation.value = Pair(lat, lon)
                _uiMessage.value = "📍 Location saved successfully!"
            } catch (e: Exception) {
                _uiMessage.value = "Failed to save location: ${e.message}"
            }
        }
    }

    fun loadProviderLocation() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val doc = firestore.collection("providers").document(uid).get().await()
                val lat = doc.getDouble("latitude") ?: 0.0
                val lon = doc.getDouble("longitude") ?: 0.0
                if (lat != 0.0 && lon != 0.0) {
                    _providerLocation.value = Pair(lat, lon)
                }
            } catch (e: Exception) {
                // Silently ignore — location just won't be pre-filled
            }
        }
    }

    private fun loadProviderName() {
        val user = auth.currentUser
        _displayName.value = user?.displayName ?: user?.email?.substringBefore("@") ?: "Provider"
    }
}