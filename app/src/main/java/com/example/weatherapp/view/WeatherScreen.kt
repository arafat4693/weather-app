package com.example.weatherapp.view

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.weatherapp.R
import com.example.weatherapp.network.NetworkResponse
import com.example.weatherapp.utils.ICON_MAP
import com.example.weatherapp.viewModel.WeatherViewModel
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.weatherapp.utils.formatDay
import com.example.weatherapp.utils.formatHour
import com.example.weatherapp.viewModel.DailyWeatherData
import com.example.weatherapp.viewModel.HourlyWeatherData
import com.example.weatherapp.viewModel.WeatherResponse
import kotlinx.coroutines.launch

@Composable
fun WeatherScreen (modifier: Modifier = Modifier, vm: WeatherViewModel) {
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var placeName by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val weatherResult = vm.weatherResult.observeAsState()
    val isInternetAvailable = vm.isInternetAvailable.observeAsState()

    val config = LocalConfiguration.current
    val mode = remember { mutableStateOf(config.orientation) }
    val landscape = mode.value == Configuration.ORIENTATION_LANDSCAPE;

    Scaffold (
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) {contentPadding -> (
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(contentPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if (isInternetAvailable.value == false) {
                    LaunchedEffect(snackbarHostState) {
                        snackbarHostState.showSnackbar("No internet connection")
                    }
                }

                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Latitude TextField
                    /*OutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = latitude,
                        onValueChange = {
                            latitude = it
                        },
                        label = { Text(text = "Latitude") },
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    // Longitude TextField
                    OutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = longitude,
                        onValueChange = {
                            longitude = it
                        },
                        label = { Text(text = "Longitude") },
                    )
                    Spacer(modifier = Modifier.width(8.dp))*/

                    // Search IconButton
                    /*IconButton(onClick = {
                        val lat = latitude.toDoubleOrNull()
                        val lon = longitude.toDoubleOrNull()
                        if (lat != null && lon != null) {
                            vm.getData(lat, lon)
                            keyboardController?.hide()
                        } else {
                            // Show Snackbar
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Please enter valid numbers for Latitude and Longitude"
                                )
                            }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search by coordinates"
                        )
                    }*/

                    OutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = placeName,
                        onValueChange = {
                            placeName = it
                        },
                        label = { Text(text = "Place Name") },
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(onClick = {
                        vm.getData(placeName)
                        keyboardController?.hide()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search by place"
                        )
                    }
                }

                /*Button(onClick = {
                    vm.fetchCoordinatesForPlace("berlin")
                }) {
                    Text(text = "Generate eventValues")
                }*/

                when(val result = weatherResult.value) {
                    is NetworkResponse.Error -> {
                        Text(text = result.message)
                    }
                    NetworkResponse.Loading -> {
                        CircularProgressIndicator()
                    }
                    is NetworkResponse.Success -> {
                        if(!landscape) {
                            WeatherDetails(data = result.data, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), false)
                            WeeklyWeather(daily = result.data.daily, modifier = Modifier.padding(8.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            HourlyWeather(hourly = result.data.hourly, false)
                        } else {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                WeatherDetails(
                                    data = result.data,
                                    modifier = Modifier
                                        .width(350.dp)
                                        .padding(end = 8.dp),
                                    true
                                )

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth() // Takes up all the remaining space
                                        .padding(start = 8.dp)
                                ) {
                                    WeeklyWeather(daily = result.data.daily, modifier = Modifier.padding(4.dp))

                                    Spacer(modifier = Modifier.height(10.dp))

                                    HourlyWeather(hourly = result.data.hourly, true)
                                }
                            }
                        }
                    }
                    null -> {}
                }
            }
            )
    }
}

@Composable
fun getPainterForWeatherCode(weatherCode: Int): Painter {
    val drawableRes = ICON_MAP[weatherCode] ?: R.drawable.sun // Fallback to default icon
    return painterResource(id = drawableRes)
}

@Composable
fun WeatherIcon(weatherCode: Int, iconSize: Int) {
    Image(
        painter = getPainterForWeatherCode(weatherCode),
        contentDescription = "Weather Icon",
        modifier = Modifier.size(iconSize.dp)
    )
}

@Composable
fun WeatherDetails(data : WeatherResponse, modifier: Modifier = Modifier, isLandscape: Boolean) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if(!isLandscape) {
            Text(
                text = " ${data.currentWeather.currentTemp} ° c",
                fontSize = (35.sp),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            WeatherIcon(weatherCode = data.currentWeather.iconCode, iconSize = 100)

            Spacer(modifier = Modifier.height(8.dp))
            Card {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        WeatherKeyVal("Precip",data.currentWeather.precip.toString()+" in")
                        WeatherKeyVal("Wind",data.currentWeather.windSpeed.toString()+" mph")
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        WeatherKeyVal("High",data.currentWeather.highTemp.toString()+" ° c")
                        WeatherKeyVal("Low",data.currentWeather.lowTemp.toString()+" ° c")
                    }
                }
            }
        } else {
            Text(
                text = " ${data.currentWeather.currentTemp} ° c",
                fontSize = (25.sp),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            WeatherIcon(weatherCode = data.currentWeather.iconCode, iconSize = 60)

            Spacer(modifier = Modifier.height(4.dp))
            Card {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        WeatherKeyVal("Precip",data.currentWeather.precip.toString()+" in")
                        WeatherKeyVal("Wind",data.currentWeather.windSpeed.toString()+" mph")
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        WeatherKeyVal("High",data.currentWeather.highTemp.toString()+" ° c")
                        WeatherKeyVal("Low",data.currentWeather.lowTemp.toString()+" ° c")
                    }
                }
            }
        }

    }
}

@Composable
fun WeatherKeyVal(key : String, value : String) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = key, fontWeight = FontWeight.SemiBold, color = Color.Gray)
    }
}

@Composable
fun WeeklyWeather(daily: List<DailyWeatherData>, modifier: Modifier = Modifier) {
    LazyRow{
        items(daily) { dayWeather ->
            Column(
                modifier = modifier
                    .width(75.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                WeatherIcon(weatherCode = dayWeather.iconCode, iconSize = 40)
                Spacer(modifier = Modifier.height(4.dp))
                Text(formatDay(dayWeather.timestamp), fontSize = 12.sp)
                Text("${dayWeather.maxTemp}°", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun HourlyWeather(hourly: List<HourlyWeatherData>, isLandscape: Boolean) {
    if(!isLandscape) {
        Text(
            text = "Hourly Weather",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
        )
    }

    Spacer(modifier = Modifier.height(10.dp))

    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(hourly) { hourWeather ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(formatHour(hourWeather.timestamp), modifier = Modifier.weight(1f))
                WeatherIcon(weatherCode = hourWeather.iconCode, iconSize = 40)

                Spacer(modifier = Modifier.width(16.dp))

                Text("${hourWeather.temp}°", modifier = Modifier.weight(1f))
                Text("${hourWeather.windSpeed} mph", modifier = Modifier.weight(1f))
                Text("${hourWeather.precip} in", modifier = Modifier.weight(1f))
            }
        }
    }
}