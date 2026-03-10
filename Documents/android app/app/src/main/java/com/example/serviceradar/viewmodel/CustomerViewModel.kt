package com.example.serviceradar.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.serviceradar.data.model.Booking
import com.example.serviceradar.data.model.BookingFilter
import com.example.serviceradar.data.model.Provider
import com.example.serviceradar.data.model.ProviderFilter
import com.example.serviceradar.data.repository.CustomerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.viewModelScope
import com.example.serviceradar.data.remote.RetrofitInstance
import kotlinx.coroutines.launch
import com.example.serviceradar.data.model.FavouriteProvider
import com.example.serviceradar.data.local.AppDatabase
import kotlinx.coroutines.Dispatchers
import com.example.serviceradar.utils.LocationManager
import com.example.serviceradar.utils.LocationUtils
import com.google.firebase.auth.FirebaseAuth

class CustomerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CustomerRepository(application)
    private val dao = AppDatabase.getInstance(application).serviceRadarDao()
    private val locationManager = LocationManager(application)

    private val _providers = MutableStateFlow<List<Provider>>(emptyList())
    val providers: StateFlow<List<Provider>> = _providers

    private val _myBookings = MutableStateFlow<List<Booking>>(emptyList())
    val myBookings: StateFlow<List<Booking>> = _myBookings

    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isNetworkAvailable = MutableStateFlow(true)
    val isNetworkAvailable: StateFlow<Boolean> = _isNetworkAvailable

    private val _apiCategories = MutableStateFlow<List<String>>(emptyList())
    val apiCategories: StateFlow<List<String>> = _apiCategories

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _filteredProviders = MutableStateFlow<List<Provider>>(emptyList())
    val filteredProviders: StateFlow<List<Provider>> = _filteredProviders

    private val _bookingFilters = MutableStateFlow<BookingFilter>(BookingFilter())
    val bookingFilters: StateFlow<BookingFilter> = _bookingFilters

    private val _providerFilter = MutableStateFlow<ProviderFilter>(ProviderFilter())
    val providerFilter: StateFlow<ProviderFilter> = _providerFilter

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _favourites = MutableStateFlow<List<FavouriteProvider>>(emptyList())
    val favourites: StateFlow<List<FavouriteProvider>> = _favourites

    private val _customerLocation = MutableStateFlow<Pair<Double, Double>?>(null)
    val customerLocation: StateFlow<Pair<Double, Double>?> = _customerLocation

    private val _nearbyProviders = MutableStateFlow<List<Provider>>(emptyList())
    val nearbyProviders: StateFlow<List<Provider>> = _nearbyProviders

    private val _isLocating = MutableStateFlow(false)
    val isLocating: StateFlow<Boolean> = _isLocating

    private val _displayName = MutableStateFlow("")
    val displayName: StateFlow<String> = _displayName

    val categories = listOf("Plumber", "Electrician", "Tutor", "Cleaner", "Carpenter")

    init {
        loadMyBookings()
        loadUserProfile()
    }

    fun setNetworkAvailable(available: Boolean) {
        _isNetworkAvailable.value = available
    }

    fun loadProvidersByCategory(category: String) {
        _isLoading.value = true
        repository.getProvidersByCategory(
            category = category,
            isOnline = _isNetworkAvailable.value
        ) { providers ->
            _providers.value = providers
            _isLoading.value = false
            recomputeNearby(providers)
        }
    }

    fun fetchCustomerLocation() {
        viewModelScope.launch {
            _isLocating.value = true
            val location = locationManager.getCurrentLocation()
            _isLocating.value = false
            if (location != null) {
                _customerLocation.value = location
                recomputeNearby(_providers.value)
            } else {
                _uiMessage.value = "📍 Could not get location. Check GPS permission."
            }
        }
    }

    private fun recomputeNearby(providers: List<Provider>) {
        val loc = _customerLocation.value ?: return
        val (cLat, cLon) = loc
        _nearbyProviders.value = providers
            .filter { LocationUtils.hasValidLocation(it.latitude, it.longitude) }
            .map { p ->
                p.copy(
                    distanceKm = LocationUtils.calculateDistance(cLat, cLon, p.latitude, p.longitude)
                )
            }
            .sortedBy { it.distanceKm }
    }

    fun loadMyBookings() {
        repository.getMyBookings(
            isOnline = _isNetworkAvailable.value
        ) { bookings ->
            _myBookings.value = bookings
        }
    }

    // ── FIXED: now actually calls repository to save booking in Firestore ──────
    fun createBooking(
        providerId: String,
        serviceCategory: String,
        scheduledDate: String = "",
        scheduledTime: String = ""
    ) {
        _isLoading.value = true
        repository.createBooking(
            providerId = providerId,
            serviceCategory = serviceCategory,
            scheduledDate = scheduledDate,
            scheduledTime = scheduledTime,
            onSuccess = {
                _isLoading.value = false
                _uiMessage.value = "✅ Booking confirmed! Provider will accept shortly."
                loadMyBookings()
            },
            onError = { error ->
                _isLoading.value = false
                _uiMessage.value = "Booking failed: $error"
            }
        )
    }

    fun loadCategoriesFromApi() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getCategories()
                _apiCategories.value = response.map { it.name }
            } catch (e: Exception) {
                _uiMessage.value = "Using local categories"
            }
        }
    }

    fun submitRating(bookingId: String, providerId: String, rating: Double) {
        repository.submitRating(
            bookingId = bookingId,
            providerId = providerId,
            rating = rating,
            onSuccess = {
                _uiMessage.value = "Rating submitted successfully!"
                loadMyBookings()
            },
            onError = { _uiMessage.value = "Failed to submit rating" }
        )
    }

    fun submitRatingAndReview(bookingId: String, providerId: String, rating: Double, review: String) {
        repository.submitRatingAndReview(
            bookingId = bookingId,
            providerId = providerId,
            rating = rating,
            review = review,
            onSuccess = {
                _uiMessage.value = "Review submitted successfully!"
                loadMyBookings()
            },
            onError = { _uiMessage.value = "Failed to submit review" }
        )
    }

    fun clearMessage() {
        _uiMessage.value = null
    }

    fun searchProviders(query: String) {
        _searchQuery.value = query
        if (query.isNotEmpty()) {
            _isLoading.value = true
            repository.searchProviders(
                query = query,
                isOnline = _isNetworkAvailable.value
            ) { providers ->
                _filteredProviders.value = providers
                _isLoading.value = false
            }
        } else {
            _filteredProviders.value = emptyList()
        }
    }

    fun filterProviders(filter: ProviderFilter) {
        _providerFilter.value = filter
        _isLoading.value = true
        repository.filterProviders(
            filter = filter,
            isOnline = _isNetworkAvailable.value
        ) { providers ->
            _filteredProviders.value = providers
            _isLoading.value = false
        }
    }

    fun filterBookingHistory(filter: BookingFilter) {
        _bookingFilters.value = filter
        repository.filterBookingHistory(
            filter = filter,
            isOnline = _isNetworkAvailable.value
        ) { bookings ->
            _myBookings.value = bookings
        }
    }

    fun reportProvider(providerId: String, reason: String, description: String) {
        repository.reportProvider(
            providerId = providerId,
            reason = reason,
            description = description,
            onSuccess = { _uiMessage.value = "Provider reported successfully. Thank you for your feedback." },
            onError = { _uiMessage.value = "Failed to report provider" }
        )
    }

    fun refreshData() {
        _isRefreshing.value = true
        loadMyBookings()
        if (_providers.value.isNotEmpty()) {
            loadProvidersByCategory(_providers.value[0].category)
        } else {
            loadProvidersByCategory("Plumber")
        }
        _isRefreshing.value = false
    }

    fun cancelBooking(bookingId: String) {
        _isLoading.value = true
        repository.cancelBooking(
            bookingId = bookingId,
            onSuccess = {
                _isLoading.value = false
                _uiMessage.value = "Booking Cancelled ❌"
                loadMyBookings()
            },
            onError = {
                _isLoading.value = false
                _uiMessage.value = "Failed to cancel booking"
            }
        )
    }

    fun loadFavourites() {
        viewModelScope.launch(Dispatchers.IO) {
            _favourites.value = dao.getAllFavourites()
        }
    }

    fun isFavourite(providerId: String): Boolean {
        return _favourites.value.any { it.providerId == providerId }
    }

    fun toggleFavourite(provider: Provider) {
        viewModelScope.launch(Dispatchers.IO) {
            val fav = _favourites.value.find { it.providerId == provider.id }
            if (fav != null) {
                dao.deleteFavourite(fav)
            } else {
                dao.insertFavourite(
                    FavouriteProvider(
                        providerId = provider.id,
                        category = provider.category,
                        price = provider.price
                    )
                )
            }
            _favourites.value = dao.getAllFavourites()
        }
    }

    fun removeFavourite(providerId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val fav = _favourites.value.find { it.providerId == providerId }
            if (fav != null) {
                dao.deleteFavourite(fav)
                _favourites.value = dao.getAllFavourites()
            }
        }
    }

    fun insertFavourite(provider: Provider) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertFavourite(
                FavouriteProvider(
                    providerId = provider.id,
                    category = provider.category,
                    price = provider.price
                )
            )
            _favourites.value = dao.getAllFavourites()
        }
    }

    private fun loadUserProfile() {
        val user = FirebaseAuth.getInstance().currentUser
        _displayName.value = user?.displayName ?: user?.email?.substringBefore("@") ?: "User"
    }
}