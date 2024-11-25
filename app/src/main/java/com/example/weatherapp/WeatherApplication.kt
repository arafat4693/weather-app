package com.example.weatherapp

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import com.example.weatherapp.network.RetrofitClient
import com.example.weatherapp.network.WeatherService
import androidx.datastore.preferences.core.Preferences
import com.example.weatherapp.network.GeocodingRetrofitClient
import com.example.weatherapp.network.GeocodingService
import com.example.weatherapp.repository.WeatherPreferencesRepository

private const val WEATHER_PREFERENCES_NAME = "weather_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = WEATHER_PREFERENCES_NAME
)

class WeatherApplication : Application() {
    lateinit var weatherPreferencesRepository: WeatherPreferencesRepository
    lateinit var weatherService : WeatherService
    lateinit var geocodingService: GeocodingService

    override fun onCreate() {
        super.onCreate()
        weatherService = RetrofitClient.weatherService
        geocodingService = GeocodingRetrofitClient.geocodingService
        weatherPreferencesRepository = WeatherPreferencesRepository(dataStore)
    }
}