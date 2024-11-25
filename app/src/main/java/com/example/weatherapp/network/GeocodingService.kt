package com.example.weatherapp.network

import com.example.weatherapp.model.GeocodingModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingService {
    @GET("search")
    suspend fun getCoordinates(
        @Query("q") placeName: String,
        @Query("api_key") apiKey: String = "674042bd17449945750417jgpfc9a10"
    ): Response<GeocodingModel>
}