package com.example.weatherapp.network

import com.example.weatherapp.model.WeatherModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("forecast?hourly=temperature_2m,apparent_temperature,precipitation,weathercode,windspeed_10m" +
            "&daily=weathercode,temperature_2m_max,temperature_2m_min,apparent_temperature_max," +
            "apparent_temperature_min,precipitation_sum&current_weather=true&temperature_unit=fahrenheit" +
            "&windspeed_unit=mph&precipitation_unit=inch&timeformat=unixtime")
    suspend fun getWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
    ): Response<WeatherModel>
}