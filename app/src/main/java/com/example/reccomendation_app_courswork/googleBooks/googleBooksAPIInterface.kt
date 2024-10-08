package com.example.reccomendation_app_courswork.googleBooks

import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBooksAPI {

    @GET("volumes")
    suspend fun getTopBooks(
        @Query("q") query: String = "bestseller",
        @Query("orderBy") orderBy: String = "relevance",
        @Query("maxResults") maxResults: Int = 10,
        @Query("startIndex") startIndex: Int = 0,
        @Query("key") apiKey: String
    ): BookResponse
}