package com.example.serviceradar.data.remote

import retrofit2.http.GET

data class CategoryResponse(
    val id: Int,
    val name: String,
    val description: String
)

interface ApiService {
    @GET("categories")
    suspend fun getCategories(): List<CategoryResponse>
}