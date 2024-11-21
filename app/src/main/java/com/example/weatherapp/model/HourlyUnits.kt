package com.example.weatherapp.model

data class HourlyUnits(
    val apparent_temperature: String,
    val precipitation: String,
    val temperature_2m: String,
    val time: String,
    val weathercode: String,
    val windspeed_10m: String
)