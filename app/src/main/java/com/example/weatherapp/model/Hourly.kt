package com.example.weatherapp.model

data class Hourly(
    val apparent_temperature: List<String>,
    val precipitation: List<String>,
    val temperature_2m: List<String>,
    val time: List<String>,
    val weathercode: List<Int>,
    val windspeed_10m: List<String>
)