package com.example.weatherapp.model

data class CurrentWeather(
    val interval: String,
    val is_day: String,
    val temperature: String,
    val time: String,
    val weathercode: Int,
    val winddirection: String,
    val windspeed: String
)