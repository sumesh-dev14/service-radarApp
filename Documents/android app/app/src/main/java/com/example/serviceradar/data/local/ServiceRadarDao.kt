package com.example.serviceradar.data.local

import androidx.room.*

@Dao
interface ServiceRadarDao {

    // Provider DAOs
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProviders(providers: List<CachedProvider>)

    @Suppress("UNUSED")
    @Query("SELECT * FROM cached_providers")
    suspend fun getAllProviders(): List<CachedProvider>

    @Query("SELECT * FROM cached_providers WHERE category = :category")
    suspend fun getProvidersByCategory(category: String): List<CachedProvider>

    @Query("DELETE FROM cached_providers")
    suspend fun clearProviders()

    // Booking DAOs
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookings(bookings: List<CachedBooking>)

    @Suppress("UNUSED")
    @Query("SELECT * FROM cached_bookings")
    suspend fun getAllBookings(): List<CachedBooking>

    @Query("SELECT * FROM cached_bookings WHERE customerId = :customerId")
    suspend fun getBookingsByCustomer(customerId: String): List<CachedBooking>

    @Query("DELETE FROM cached_bookings")
    suspend fun clearBookings()

    // Analytics DAOs
    @Suppress("UNUSED")
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnalytics(analytics: CachedAnalytics)

    @Suppress("UNUSED")
    @Query("SELECT * FROM cached_analytics WHERE providerId = :providerId")
    suspend fun getAnalytics(providerId: String): CachedAnalytics?

    @Suppress("UNUSED")
    @Query("DELETE FROM cached_analytics")
    suspend fun clearAnalytics()

    // Provider Report DAOs
    @Suppress("UNUSED")
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: CachedProviderReport)

    @Suppress("UNUSED")
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReports(reports: List<CachedProviderReport>)

    @Suppress("UNUSED")
    @Query("SELECT * FROM provider_reports WHERE providerId = :providerId")
    suspend fun getReportsByProvider(providerId: String): List<CachedProviderReport>

    @Suppress("UNUSED")
    @Query("SELECT * FROM provider_reports WHERE status = :status")
    suspend fun getReportsByStatus(status: String): List<CachedProviderReport>

    @Suppress("UNUSED")
    @Query("DELETE FROM provider_reports")
    suspend fun clearReports()

    // Favourites DAOs
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavourite(favourite: com.example.serviceradar.data.model.FavouriteProvider)

    @Delete
    suspend fun deleteFavourite(favourite: com.example.serviceradar.data.model.FavouriteProvider)

    @Query("SELECT * FROM favourite_providers ORDER BY savedAt DESC")
    suspend fun getAllFavourites(): List<com.example.serviceradar.data.model.FavouriteProvider>

    @Query("SELECT EXISTS(SELECT 1 FROM favourite_providers WHERE providerId = :providerId)")
    suspend fun isFavourite(providerId: String): Boolean
}
