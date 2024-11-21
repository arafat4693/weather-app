package com.example.weatherapp.utils

import android.annotation.SuppressLint
import android.util.Log
import com.example.weatherapp.model.WeatherModel
import com.example.weatherapp.viewModel.CurrentWeatherData
import com.example.weatherapp.viewModel.DailyWeatherData
import com.example.weatherapp.viewModel.HourlyWeatherData
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

fun parseCurrentWeather(response: WeatherModel): CurrentWeatherData {
    val current = response.current_weather
    val daily = response.daily

    Log.i("lg: ", "parseCurrentWeather")

    return CurrentWeatherData(
        current.temperature.toDouble().roundToInt(),
        daily.temperature_2m_max[0].toDouble().roundToInt(),
        daily.temperature_2m_min[0].toDouble().roundToInt(),
        daily.apparent_temperature_max[0].toDouble().roundToInt(),
        daily.apparent_temperature_min[0].toDouble().roundToInt(),
        current.windspeed.toDouble().roundToInt(),
        "%.2f".format(daily.precipitation_sum[0].toDouble()).toDouble(),
        current.weathercode
    )
}

fun parseDailyWeather(response: WeatherModel): List<DailyWeatherData> {
    Log.i("lg: ", "parseDailyWeather")
    return response.daily.time.mapIndexed { index, time ->
        DailyWeatherData(
            time.toLong() * 1000, // Convert to milliseconds
            response.daily.weathercode[index],
            response.daily.temperature_2m_max[index].toDouble().roundToInt()
        )
    }
}

fun parseHourlyWeather(response: WeatherModel): List<HourlyWeatherData> {
    Log.i("lg: ", "parseHourlyWeather")
    return response.hourly.time.mapIndexed { index, time ->
        HourlyWeatherData(
            time.toLong() * 1000, // Convert to milliseconds
            response.hourly.weathercode[index],
            response.hourly.temperature_2m[index].toDouble().roundToInt(),
            response.hourly.apparent_temperature[index].toDouble().roundToInt(),
            response.hourly.windspeed_10m[index].toDouble().roundToInt(),
            "%.2f".format(response.hourly.precipitation[index].toDouble()).toDouble()
        )
    }.filter { it.timestamp >= response.current_weather.time.toLong() * 1000 }
}

@SuppressLint("NewApi")
fun formatDay(timestamp: Long): String {
    // Create a formatter for the day of the week
    val dayFormatter = DateTimeFormatter.ofPattern("EEEE")
        .withZone(ZoneId.systemDefault())

    // Convert the timestamp to a readable day
    return dayFormatter.format(Instant.ofEpochMilli(timestamp))
}

@SuppressLint("NewApi")
fun formatHour(timestamp: Long): String {
    // Create a formatter for the hour
    val hourFormatter = DateTimeFormatter.ofPattern("h a")
        .withZone(ZoneId.systemDefault())

    // Convert the timestamp to a readable hour format
    return hourFormatter.format(Instant.ofEpochMilli(timestamp))
}