package com.example.app.data.repository

import com.example.app.data.api.ApiResult
import com.example.app.data.api.GeocodingApi
import com.example.app.data.api.WeatherApi
import com.example.app.data.local.FavoriteLocationDao
import com.example.app.data.local.toEntity
import com.example.app.data.local.toLocation
import com.example.app.data.model.DailyForecast
import com.example.app.data.model.HourlyForecast
import com.example.app.data.model.Location
import com.example.app.data.model.WeatherDetail
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val geocodingApi: GeocodingApi,
    private val weatherApi: WeatherApi,
    private val favoriteLocationDao: FavoriteLocationDao
) : WeatherRepositoryContract {


    override suspend fun searchLocations(query: String): ApiResult<List<Location>> {
        return try {
            val response = geocodingApi.searchLocations(query = query)
            val locations = response.results.orEmpty()
            ApiResult.Success(locations)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            ApiResult.Error("Network error: ${e.message}")
        }
    }

    override suspend fun getWeather(location: Location): ApiResult<WeatherDetail> {
        return try {
            val weatherResponse = weatherApi.getWeather(
                latitude = location.latitude,
                longitude = location.longitude
            )

            val hourlyForecast = weatherResponse.hourly.time
                .take(24)
                .mapIndexed { index, time ->
                    HourlyForecast(
                        time = time,
                        temperature = weatherResponse.hourly.temperature.getOrNull(index) ?: 0.0,
                        weatherCode = weatherResponse.hourly.weathercode.getOrNull(index) ?: 0,
                        windSpeed = weatherResponse.hourly.windspeed.getOrNull(index) ?: 0.0
                    )
                }

            val dailyForecast = weatherResponse.daily.time
                .mapIndexed { index, date ->
                    DailyForecast(
                        date = date,
                        maxTemp = weatherResponse.daily.tempMax.getOrNull(index) ?: 0.0,
                        minTemp = weatherResponse.daily.tempMin.getOrNull(index) ?: 0.0,
                        weatherCode = weatherResponse.daily.weathercode.getOrNull(index) ?: 0
                    )
                }

            ApiResult.Success(
                WeatherDetail(
                    location = location,
                    current = weatherResponse.currentWeather,
                    hourlyForecast = hourlyForecast,
                    dailyForecast = dailyForecast
                )
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            ApiResult.Error("Failed to load weather: ${e.message}")
        }
    }

    override suspend fun getFavorites(): List<Location> {
        return favoriteLocationDao.getAllFavorites().map { it.toLocation() }
    }

    override suspend fun toggleFavorite(location: Location) {
        if (favoriteLocationDao.isFavorite(location.id)) {
            favoriteLocationDao.deleteFavoriteById(location.id)
        } else {
            favoriteLocationDao.insertFavorite(location.toEntity())
        }
    }
}