package com.example.weatherapp.viewModel

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.weatherapp.WeatherApplication
import com.example.weatherapp.network.GeocodingService
import com.example.weatherapp.network.NetworkResponse
import com.example.weatherapp.utils.parseCurrentWeather
import com.example.weatherapp.utils.parseDailyWeather
import com.example.weatherapp.utils.parseHourlyWeather
import kotlinx.coroutines.launch
import com.example.weatherapp.network.WeatherService
import com.example.weatherapp.repository.WeatherPreferencesRepository
import kotlinx.coroutines.flow.first

class WeatherViewModel(
    private val weatherService: WeatherService,
    private val repository: WeatherPreferencesRepository,
    private val appContext: Context,
    private val geocodingService: GeocodingService
) : ViewModel() {
    //private val weatherService = RetrofitClient.weatherService

    private val _weatherResult = MutableLiveData<NetworkResponse<WeatherResponse>>()
    val weatherResult : LiveData<NetworkResponse<WeatherResponse>> = _weatherResult

    private val _isInternetAvailable = MutableLiveData<Boolean>()
    val isInternetAvailable: LiveData<Boolean> = _isInternetAvailable

    init {
        // Load persisted weather data on initialization
        viewModelScope.launch {
            repository.weatherData.collect { weatherData ->
                weatherData?.let {
                    _weatherResult.value = NetworkResponse.Success(it)
                }
            }
        }
    }

    private suspend fun fetchCoordinatesForPlace(placeName: String) {
        /*viewModelScope.launch {
            val response = geocodingService.getCoordinates(placeName)
            Log.i("geo: ", response.body()?.first().toString())
        }*/

        try {
            val response = geocodingService.getCoordinates(placeName)

            if(response.body()?.isNotEmpty() == true) {
                val firstResult = response.body()!!.first()
                val lat = firstResult.lat.toDoubleOrNull()
                val lon = firstResult.lon.toDoubleOrNull()

                if (lat != null && lon != null) {
                    fetchWeatherFromNetwork(lat, lon)
                } else {
                    _weatherResult.value = NetworkResponse.Error("Invalid location coordinates.")
                }
            } else {
                _weatherResult.value = NetworkResponse.Error("Place not found.")
            }
        }catch (e: Exception) {
            _weatherResult.value = NetworkResponse.Error("Failed to load data")
        }
    }

    fun getData(placeName: String) {
        _weatherResult.value = NetworkResponse.Loading
        viewModelScope.launch {
            val networkAvailable = isNetworkAvailable()
            _isInternetAvailable.value = networkAvailable

            if (networkAvailable) {
                //fetchWeatherFromNetwork(lat, lon)
                fetchCoordinatesForPlace(placeName)
            } else {
                fetchWeatherFromCache()
            }
        }
    }

    private suspend fun fetchWeatherFromNetwork(lat: Double, lon: Double) {
        try{
            val response = weatherService.getWeather(lat, lon)
            if(response.isSuccessful) {
                Log.i("Response : ", response.body().toString())
                response.body()?.let {
                    val weatherResponse = WeatherResponse(
                        currentWeather = parseCurrentWeather(it),
                        daily = parseDailyWeather(it),
                        hourly = parseHourlyWeather(it)
                    )
                    _weatherResult.value = NetworkResponse.Success(weatherResponse)

                    // Save the data persistently
                    repository.saveCoordinates(lat, lon)
                    repository.saveWeatherData(weatherResponse)
                }
            }else {
                //Log.i("Error : ", response.message())
                _weatherResult.value = NetworkResponse.Error("Failed to load data")
            }
        }catch(e : Exception) {
            _weatherResult.value = NetworkResponse.Error("Failed to load data")
        }
    }

    private suspend fun fetchWeatherFromCache() {
        val cachedData = repository.weatherData.first()
        if (cachedData != null) {
            _weatherResult.value = NetworkResponse.Success(cachedData)
        } else {
            _weatherResult.value = NetworkResponse.Error("No internet connection and no cached data available")
        }
    }

    // Check for network availability using context
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as WeatherApplication)
                WeatherViewModel(
                    application.weatherService,
                    application.weatherPreferencesRepository,
                    application.applicationContext,
                    application.geocodingService
                )
            }
        }
    }

}

// Current Weather Data Model
data class CurrentWeatherData(
    val currentTemp: Int,
    val highTemp: Int,
    val lowTemp: Int,
    val highFeelsLike: Int,
    val lowFeelsLike: Int,
    val windSpeed: Int,
    val precip: Double,
    val iconCode: Int
)

// Daily Weather Data Model
data class DailyWeatherData(
    val timestamp: Long,
    val iconCode: Int,
    val maxTemp: Int
)

// Hourly Weather Data Model
data class HourlyWeatherData(
    val timestamp: Long,
    val iconCode: Int,
    val temp: Int,
    val feelsLike: Int,
    val windSpeed: Int,
    val precip: Double
)

// Full Weather API Response
data class WeatherResponse(
    val currentWeather: CurrentWeatherData,
    val daily: List<DailyWeatherData>,
    val hourly: List<HourlyWeatherData>
)