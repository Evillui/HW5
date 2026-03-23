package com.example.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.data.api.ApiResult
import com.example.app.data.model.Location
import com.example.app.data.model.WeatherDetail
import com.example.app.data.repository.WeatherRepositoryContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepositoryContract
) : ViewModel() {

    var uiState by mutableStateOf(WeatherUiState())
        private set

    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            loadFavorites()
        }
    }

    fun onEvent(event: WeatherEvent) {
        when (event) {
            is WeatherEvent.SearchLocation -> searchLocations(event.query)
            is WeatherEvent.OpenDetail -> openDetail(event.id)
            is WeatherEvent.ToggleFavorite -> toggleFavorite(event.location)

            WeatherEvent.ClearSearch -> {
                searchJob?.cancel()
                uiState = uiState.copy(
                    searchQuery = "",
                    locations = emptyList(),
                    isEmpty = false,
                    error = null,
                    isLoading = false
                )
            }

            WeatherEvent.RefreshWeather -> {
                if (uiState.searchQuery.length >= 2) {
                    searchLocations(uiState.searchQuery)
                }
            }
        }
    }

    private suspend fun loadFavorites() {
        val favorites = repository.getFavorites()
        uiState = uiState.copy(
            favorites = favorites,
            favoriteIds = favorites.map { it.id }.toSet()
        )
    }

    private fun searchLocations(query: String) {
        searchJob?.cancel()

        uiState = uiState.copy(
            searchQuery = query,
            error = null
        )

        if (query.length < 2) {
            uiState = uiState.copy(
                locations = emptyList(),
                isLoading = false,
                isEmpty = false
            )
            return
        }

        searchJob = viewModelScope.launch {
            delay(400)

            uiState = uiState.copy(
                isLoading = true,
                error = null,
                isEmpty = false
            )

            when (val result = repository.searchLocations(query)) {
                is ApiResult.Success -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        locations = result.data,
                        isEmpty = result.data.isEmpty(),
                        error = null
                    )
                }

                is ApiResult.Error -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        error = result.message,
                        locations = emptyList(),
                        isEmpty = false
                    )
                }

                else -> Unit
            }
        }
    }

    private fun openDetail(id: Int) {
        val loc = uiState.locations.firstOrNull { it.id == id }
            ?: uiState.favorites.firstOrNull { it.id == id }

        if (loc == null) {
            uiState = uiState.copy(
                errorDetail = "Location not found",
                weatherDetail = null,
                isLoadingDetail = false,
                selectedLocation = null
            )
            return
        }

        loadWeather(loc)
    }

    private fun loadWeather(location: Location) {
        viewModelScope.launch {
            uiState = uiState.copy(
                selectedLocation = location,
                isLoadingDetail = true,
                errorDetail = null
            )

            when (val result = repository.getWeather(location)) {
                is ApiResult.Success -> {
                    uiState = uiState.copy(
                        isLoadingDetail = false,
                        weatherDetail = result.data,
                        errorDetail = null
                    )
                }

                is ApiResult.Error -> {
                    uiState = uiState.copy(
                        isLoadingDetail = false,
                        errorDetail = result.message,
                        weatherDetail = null
                    )
                }

                else -> Unit
            }
        }
    }

    private fun toggleFavorite(location: Location) {
        viewModelScope.launch {
            repository.toggleFavorite(location)
            loadFavorites()
        }
    }
}

data class WeatherUiState(
    val isLoading: Boolean = false,
    val locations: List<Location> = emptyList(),
    val favorites: List<Location> = emptyList(),
    val favoriteIds: Set<Int> = emptySet(),
    val error: String? = null,
    val searchQuery: String = "",
    val isEmpty: Boolean = false,
    val isLoadingDetail: Boolean = false,
    val weatherDetail: WeatherDetail? = null,
    val errorDetail: String? = null,
    val selectedLocation: Location? = null
)