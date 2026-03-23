package com.example.app.data.repository

import com.example.app.data.api.ApiResult
import com.example.app.data.model.Location
import com.example.app.data.model.WeatherDetail

interface WeatherRepositoryContract {
    suspend fun searchLocations(query: String): ApiResult<List<Location>>
    suspend fun getWeather(location: Location): ApiResult<WeatherDetail>
    suspend fun getFavorites(): List<Location>
    suspend fun toggleFavorite(location: Location)
}