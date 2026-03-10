package com.example.serviceradar


import android.app.Application
import com.example.serviceradar.data.local.AppDatabase

class ServiceRadarApp : Application() {

    val database: AppDatabase by lazy {
        AppDatabase.getInstance(this)
    }
}