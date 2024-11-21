package com.example.weatherapp.model

data class Daily(
    val apparent_temperature_max: List<String>,
    val apparent_temperature_min: List<String>,
    val precipitation_sum: List<String>,
    val temperature_2m_max: List<String>,
    val temperature_2m_min: List<String>,
    val time: List<String>,
    val weathercode: List<Int>
)