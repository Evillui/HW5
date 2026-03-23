package com.example.app.viewmodel

import com.example.app.MainDispatcherRule
import com.example.app.FakeWeatherRepository
import com.example.app.data.api.ApiResult
import com.example.app.data.model.Location
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherNonTrivialTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val vladivostok = Location(
        id = 1,
        name = "Vladivostok",
        latitude = 43.1155,
        longitude = 131.8855,
        country = "Russia",
        region = "Primorsky Krai",
        timezone = "Asia/Vladivostok"
    )

    @Test
    fun retry_really_starts_new_request() = runTest {
        val repository = FakeWeatherRepository().apply {
            queuedSearchResults.add(ApiResult.Error("Temporary failure"))
            queuedSearchResults.add(ApiResult.Success(listOf(vladivostok)))
        }
        val viewModel = WeatherViewModel(repository)

        viewModel.onEvent(WeatherEvent.SearchLocation("vla"))
        advanceUntilIdle()

        assertEquals(1, repository.searchCalls)
        assertEquals("Temporary failure", viewModel.uiState.error)

        viewModel.onEvent(WeatherEvent.RefreshWeather)
        advanceUntilIdle()

        assertEquals(2, repository.searchCalls)
        assertEquals("vla", repository.lastSearchQuery)
        assertNull(viewModel.uiState.error)
        assertEquals(1, viewModel.uiState.locations.size)
        assertEquals("Vladivostok", viewModel.uiState.locations.first().name)
    }

    @Test
    fun toggle_favorite_twice_returns_to_empty_without_duplicates() = runTest {
        val repository = FakeWeatherRepository()
        val viewModel = WeatherViewModel(repository)

        advanceUntilIdle()
        assertTrue(viewModel.uiState.favorites.isEmpty())
        assertTrue(viewModel.uiState.favoriteIds.isEmpty())

        viewModel.onEvent(WeatherEvent.ToggleFavorite(vladivostok))
        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.favorites.size)
        assertEquals(setOf(1), viewModel.uiState.favoriteIds)

        viewModel.onEvent(WeatherEvent.ToggleFavorite(vladivostok))
        advanceUntilIdle()

        assertTrue(viewModel.uiState.favorites.isEmpty())
        assertTrue(viewModel.uiState.favoriteIds.isEmpty())
    }
}