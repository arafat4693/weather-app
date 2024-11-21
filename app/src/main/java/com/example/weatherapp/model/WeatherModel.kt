package com.example.weatherapp.model

data class WeatherModel(
    val current_weather: CurrentWeather,
    val current_weather_units: CurrentWeatherUnits,
    val daily: Daily,
    val daily_units: DailyUnits,
    val elevation: String,
    val generationtime_ms: String,
    val hourly: Hourly,
    val hourly_units: HourlyUnits,
    val latitude: String,
    val longitude: String,
    val timezone: String,
    val timezone_abbreviation: String,
    val utc_offset_seconds: String
)