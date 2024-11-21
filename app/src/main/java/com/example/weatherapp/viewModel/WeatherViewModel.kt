package com.example.weatherapp.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.WeatherModel
import com.example.weatherapp.network.NetworkResponse
import com.example.weatherapp.network.RetrofitClient
import com.example.weatherapp.utils.parseCurrentWeather
import com.example.weatherapp.utils.parseDailyWeather
import com.example.weatherapp.utils.parseHourlyWeather
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    private val weatherService = RetrofitClient.weatherService

    private val _weatherResult = MutableLiveData<NetworkResponse<WeatherResponse>>()
    val weatherResult : LiveData<NetworkResponse<WeatherResponse>> = _weatherResult

    fun getData(lat : Double, lon : Double) {
        // Log.i("city name: ", city)
        _weatherResult.value = NetworkResponse.Loading
        viewModelScope.launch {
            try{
                val response = weatherService.getWeather(lat, lon)
                if(response.isSuccessful) {
                    Log.i("Response : ", response.body().toString())
                    response.body()?.let {
                        _weatherResult.value = NetworkResponse.Success(
                            WeatherResponse(
                                currentWeather = parseCurrentWeather(it),
                                daily = parseDailyWeather(it),
                                hourly = parseHourlyWeather(it)
                            )
                        )
                    }
                }else {
                    //Log.i("Error : ", response.message())
                    _weatherResult.value = NetworkResponse.Error("Failed to load data")
                }
            }catch(e : Exception) {
                _weatherResult.value = NetworkResponse.Error("Failed to load data")
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