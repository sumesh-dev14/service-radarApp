package com.example.serviceradar.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ThemeViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences = application.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    private val isDarkModeKey = "is_dark_mode"

    private val _isDarkMode = MutableStateFlow(sharedPreferences.getBoolean(isDarkModeKey, false))
    val isDarkMode: StateFlow<Boolean> = _isDarkMode

    fun toggleDarkMode() {
        val newValue = !_isDarkMode.value
        _isDarkMode.value = newValue
        saveDarkModePreference(newValue)
    }

    fun setDarkMode(isDark: Boolean) {
        _isDarkMode.value = isDark
        saveDarkModePreference(isDark)
    }

    private fun saveDarkModePreference(isDark: Boolean) {
        sharedPreferences.edit().putBoolean(isDarkModeKey, isDark).apply()
    }
}



