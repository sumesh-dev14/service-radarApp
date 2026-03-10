package com.example.serviceradar.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.serviceradar.data.model.FavouriteProvider

@Database(
    entities = [
        CachedProvider::class,
        CachedBooking::class,
        CachedAnalytics::class,
        CachedProviderReport::class,
        FavouriteProvider::class
    ],
    version = 4,           // ← bumped from 3 to 4 (new location columns in CachedProvider)
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun serviceRadarDao(): ServiceRadarDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "service_radar_db"
                )
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}