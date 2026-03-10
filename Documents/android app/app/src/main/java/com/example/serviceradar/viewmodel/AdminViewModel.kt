package com.example.serviceradar.viewmodel

import androidx.lifecycle.ViewModel
import com.example.serviceradar.data.model.Booking
import com.example.serviceradar.data.model.Provider
import com.example.serviceradar.data.model.User
import com.example.serviceradar.data.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AdminViewModel : ViewModel() {

    private val repository = AdminRepository()

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    private val _providers = MutableStateFlow<List<Provider>>(emptyList())
    val providers: StateFlow<List<Provider>> = _providers

    private val _bookings = MutableStateFlow<List<Booking>>(emptyList())
    val bookings: StateFlow<List<Booking>> = _bookings

    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadAll()
    }

    fun loadAll() {
        loadUsers()
        loadProviders()
        loadBookings()
    }

    private fun loadUsers() {
        _isLoading.value = true
        repository.getAllUsers { users ->
            _users.value = users
            _isLoading.value = false
        }
    }

    private fun loadProviders() {
        repository.getAllProviders { providers ->
            _providers.value = providers
        }
    }

    private fun loadBookings() {
        repository.getAllBookings { bookings ->
            _bookings.value = bookings
        }
    }

    fun removeProvider(providerId: String) {
        repository.removeProvider(
            providerId = providerId,
            onSuccess = {
                _uiMessage.value = "Provider removed successfully"
                loadProviders()
            },
            onError = {
                _uiMessage.value = "Failed to remove provider"
            }
        )
    }

    fun clearMessage() {
        _uiMessage.value = null
    }
}