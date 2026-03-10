package com.example.serviceradar.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.serviceradar.data.model.Booking
import com.example.serviceradar.data.model.Provider
import com.example.serviceradar.data.repository.ProviderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*

data class EarningCard(
    val serviceCategory: String,
    val date: Long,
    val amount: Double,
    val bookingId: String
)

class ProviderEarningsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProviderRepository()

    private val _providerProfile = MutableStateFlow<Provider?>(null)
    val providerProfile: StateFlow<Provider?> = _providerProfile

    private val _completedBookings = MutableStateFlow<List<Booking>>(emptyList())
    val completedBookings: StateFlow<List<Booking>> = _completedBookings

    private val _earningCards = MutableStateFlow<List<EarningCard>>(emptyList())
    val earningCards: StateFlow<List<EarningCard>> = _earningCards

    private val _totalEarnings = MutableStateFlow(0.0)
    val totalEarnings: StateFlow<Double> = _totalEarnings

    private val _monthlyEarnings = MutableStateFlow(0.0)
    val monthlyEarnings: StateFlow<Double> = _monthlyEarnings

    private val _completedJobs = MutableStateFlow(0)
    val completedJobs: StateFlow<Int> = _completedJobs

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage

    init {
        loadEarningsData()
    }

    fun loadEarningsData() {
        _isLoading.value = true

        // Load provider profile
        repository.getProviderProfile { provider ->
            _providerProfile.value = provider
        }

        // Load completed bookings
        repository.getCompletedBookings { bookings ->
            _completedBookings.value = bookings
            calculateEarnings(bookings)
            _isLoading.value = false
        }
    }

    private fun calculateEarnings(bookings: List<Booking>) {
        val providerPrice = _providerProfile.value?.price ?: 0.0
        val earningCardsList = mutableListOf<EarningCard>()
        var total = 0.0
        var monthlyTotal = 0.0
        var completedCount = 0

        val currentCalendar = Calendar.getInstance()
        val currentMonth = currentCalendar.get(Calendar.MONTH)
        val currentYear = currentCalendar.get(Calendar.YEAR)

        bookings.forEach { booking ->
            if (booking.status == "completed" && booking.completedAt != null) {
                val amount = booking.price.takeIf { it > 0 } ?: providerPrice
                total += amount
                completedCount++

                // Check if booking is from current month
                val bookingCalendar = Calendar.getInstance()
                bookingCalendar.timeInMillis = booking.completedAt
                val bookingMonth = bookingCalendar.get(Calendar.MONTH)
                val bookingYear = bookingCalendar.get(Calendar.YEAR)

                if (bookingMonth == currentMonth && bookingYear == currentYear) {
                    monthlyTotal += amount
                }

                earningCardsList.add(
                    EarningCard(
                        serviceCategory = booking.serviceCategory,
                        date = booking.completedAt,
                        amount = amount,
                        bookingId = booking.id
                    )
                )
            }
        }

        // Sort by date descending (most recent first)
        earningCardsList.sortByDescending { it.date }

        _earningCards.value = earningCardsList
        _totalEarnings.value = total
        _monthlyEarnings.value = monthlyTotal
        _completedJobs.value = completedCount
    }


    fun clearMessage() {
        _uiMessage.value = null
    }
}

