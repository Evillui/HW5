package com.example.app

import com.example.app.data.api.ApiResult
import com.example.app.data.model.Location
import com.example.app.data.model.WeatherDetail
import com.example.app.data.repository.WeatherRepositoryContract

class FakeWeatherRepository : WeatherRepositoryContract {

    val queuedSearchResults = ArrayDeque<ApiResult<List<Location>>>()
    val queuedWeatherResults = ArrayDeque<ApiResult<WeatherDetail>>()

    private val favoritesMap = linkedMapOf<Int, Location>()

    var searchCalls = 0
        private set

    var weatherCalls = 0
        private set

    var lastSearchQuery: String? = null
        private set

    var lastWeatherLocation: Location? = null
        private set

    override suspend fun searchLocations(query: String): ApiResult<List<Location>> {
        searchCalls++
        lastSearchQuery = query
        return if (queuedSearchResults.isNotEmpty()) {
            queuedSearchResults.removeFirst()
        } else {
            ApiResult.Success(emptyList())
        }
    }

    override suspend fun getWeather(location: Location): ApiResult<WeatherDetail> {
        weatherCalls++
        lastWeatherLocation = location
        return if (queuedWeatherResults.isNotEmpty()) {
            queuedWeatherResults.removeFirst()
        } else {
            error("No queued weather result")
        }
    }

    override suspend fun getFavorites(): List<Location> {
        return favoritesMap.values.toList()
    }

    override suspend fun toggleFavorite(location: Location) {
        if (favoritesMap.containsKey(location.id)) {
            favoritesMap.remove(location.id)
        } else {
            favoritesMap[location.id] = location
        }
    }
}