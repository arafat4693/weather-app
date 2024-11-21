package com.example.weatherapp.model

data class DailyUnits(
    val apparent_temperature_max: String,
    val apparent_temperature_min: String,
    val precipitation_sum: String,
    val temperature_2m_max: String,
    val temperature_2m_min: String,
    val time: String,
    val weathercode: String
)