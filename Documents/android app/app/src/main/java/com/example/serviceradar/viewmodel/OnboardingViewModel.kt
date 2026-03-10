package com.example.serviceradar.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class OnboardingViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val PREFS_NAME = "service_radar_prefs"
        private const val KEY_ONBOARDING_COMPLETE = "onboarding_complete"
    }

    private val prefs = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage

    fun setCurrentPage(page: Int) {
        _currentPage.value = page
    }

    fun hasSeenOnboarding(): Boolean {
        return prefs.getBoolean(KEY_ONBOARDING_COMPLETE, false)
    }

    fun markOnboardingComplete() {
        prefs.edit().apply {
            putBoolean(KEY_ONBOARDING_COMPLETE, true)
            apply()
        }
    }
}

