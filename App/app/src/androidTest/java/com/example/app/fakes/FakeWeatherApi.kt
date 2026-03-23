package com.example.app.fakes

import com.example.app.data.api.WeatherApi
import com.example.app.data.model.CurrentWeather
import com.example.app.data.model.DailyWeather
import com.example.app.data.model.HourlyWeather
import com.example.app.data.model.WeatherResponse

class FakeWeatherApi : WeatherApi {

    override suspend fun getWeather(
        latitude: Double,
        longitude: Double,
        currentWeather: Boolean,
        hourly: String,
        daily: String,
        timezone: String
    ): WeatherResponse {
        return WeatherResponse(
            latitude = latitude,
            longitude = longitude,
            timezone = "Asia/Vladivostok",
            currentWeather = CurrentWeather(
                time = "2026-03-22T12:00",
                temperature = 6.0,
                weathercode = 1,
                windspeed = 7.0,
                winddirection = 135,
                isDay = 1
            ),
            hourly = HourlyWeather(
                time = listOf("2026-03-22T13:00"),
                temperature = listOf(7.0),
                weathercode = listOf(1),
                windspeed = listOf(7.5)
            ),
            daily = DailyWeather(
                time = listOf("2026-03-24"),
                tempMax = listOf(9.0),
                tempMin = listOf(2.0),
                weathercode = listOf(2)
            )
        )
    }
}