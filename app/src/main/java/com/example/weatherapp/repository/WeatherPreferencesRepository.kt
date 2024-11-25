package com.example.weatherapp.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.weatherapp.viewModel.WeatherResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class WeatherPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {

    private companion object {
        val LATITUDE = doublePreferencesKey("latitude")
        val LONGITUDE = doublePreferencesKey("longitude")
        val WEATHER_DATA = stringPreferencesKey("weather_data")
        const val TAG = "WeatherPreferencesRepo"
    }

    private val gson = Gson()

    // Retrieve the stored latitude and longitude as a Flow
    val coordinates: Flow<Pair<Double, Double>?> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val lat = preferences[LATITUDE]
            val lon = preferences[LONGITUDE]
            if (lat != null && lon != null) lat to lon else null
        }

    // Retrieve the stored weather data as a Flow
    val weatherData: Flow<WeatherResponse?> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[WEATHER_DATA]?.let { json ->
                gson.fromJson(json, WeatherResponse::class.java)
            }
        }

    // Save latitude and longitude
    suspend fun saveCoordinates(lat: Double, lon: Double) {
        dataStore.edit { preferences ->
            preferences[LATITUDE] = lat
            preferences[LONGITUDE] = lon
        }
    }

    // Save weather data as a JSON string
    suspend fun saveWeatherData(weatherResponse: WeatherResponse) {
        dataStore.edit { preferences ->
            preferences[WEATHER_DATA] = gson.toJson(weatherResponse)
        }
    }
}