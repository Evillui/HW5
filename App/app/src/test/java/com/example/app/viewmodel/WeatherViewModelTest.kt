package com.example.app.viewmodel

import com.example.app.MainDispatcherRule
import com.example.app.data.api.ApiResult
import com.example.app.data.model.CurrentWeather
import com.example.app.data.model.DailyForecast
import com.example.app.data.model.HourlyForecast
import com.example.app.data.model.Location
import com.example.app.data.model.WeatherDetail
import com.example.app.FakeWeatherRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

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

    private val detail = WeatherDetail(
        location = vladivostok,
        current = CurrentWeather(
            time = "2026-03-22T12:00",
            temperature = 6.0,
            weathercode = 1,
            windspeed = 7.0,
            winddirection = 135,
            isDay = 1
        ),
        hourlyForecast = listOf(
            HourlyForecast("2026-03-22T13:00", 7.0, 1, 7.5)
        ),
        dailyForecast = listOf(
            DailyForecast("2026-03-23", 9.0, 2.0, 2)
        )
    )


    @Test
    fun initial_state_is_correct() = runTest {
        val repository = FakeWeatherRepository()
        val viewModel = WeatherViewModel(repository)

        advanceUntilIdle()

        assertFalse(viewModel.uiState.isLoading)
        assertTrue(viewModel.uiState.locations.isEmpty())
        assertTrue(viewModel.uiState.favorites.isEmpty())
        assertTrue(viewModel.uiState.favoriteIds.isEmpty())
        assertNull(viewModel.uiState.error)
        assertEquals("", viewModel.uiState.searchQuery)
        assertFalse(viewModel.uiState.isEmpty)
        assertFalse(viewModel.uiState.isLoadingDetail)
        assertNull(viewModel.uiState.weatherDetail)
        assertNull(viewModel.uiState.errorDetail)
        assertNull(viewModel.uiState.selectedLocation)
    }

    @Test
    fun search_success_updates_locations() = runTest {
        val repository = FakeWeatherRepository().apply {
            queuedSearchResults.add(ApiResult.Success(listOf(vladivostok)))
        }
        val viewModel = WeatherViewModel(repository)

        viewModel.onEvent(WeatherEvent.SearchLocation("vla"))
        advanceUntilIdle()

        assertEquals("vla", viewModel.uiState.searchQuery)
        assertFalse(viewModel.uiState.isLoading)
        assertNull(viewModel.uiState.error)
        assertFalse(viewModel.uiState.isEmpty)
        assertEquals(1, viewModel.uiState.locations.size)
        assertEquals("Vladivostok", viewModel.uiState.locations.first().name)
    }

    @Test
    fun search_error_sets_error_state() = runTest {
        val repository = FakeWeatherRepository().apply {
            queuedSearchResults.add(ApiResult.Error("Network error"))
        }
        val viewModel = WeatherViewModel(repository)

        viewModel.onEvent(WeatherEvent.SearchLocation("vla"))
        advanceUntilIdle()

        assertFalse(viewModel.uiState.isLoading)
        assertEquals("Network error", viewModel.uiState.error)
        assertTrue(viewModel.uiState.locations.isEmpty())
        assertFalse(viewModel.uiState.isEmpty)
    }

    @Test
    fun retry_after_error_makes_new_request_and_recovers() = runTest {
        val repository = FakeWeatherRepository().apply {
            queuedSearchResults.add(ApiResult.Error("Temporary failure"))
            queuedSearchResults.add(ApiResult.Success(listOf(vladivostok)))
        }
        val viewModel = WeatherViewModel(repository)

        viewModel.onEvent(WeatherEvent.SearchLocation("vla"))
        advanceUntilIdle()

        assertEquals("Temporary failure", viewModel.uiState.error)
        assertEquals(1, repository.searchCalls)

        viewModel.onEvent(WeatherEvent.RefreshWeather)
        advanceUntilIdle()

        assertEquals(2, repository.searchCalls)
        assertNull(viewModel.uiState.error)
        assertEquals(1, viewModel.uiState.locations.size)
        assertEquals("Vladivostok", viewModel.uiState.locations.first().name)
    }

    @Test
    fun empty_result_sets_empty_state() = runTest {
        val repository = FakeWeatherRepository().apply {
            queuedSearchResults.add(ApiResult.Success(emptyList()))
        }
        val viewModel = WeatherViewModel(repository)

        viewModel.onEvent(WeatherEvent.SearchLocation("zzzz"))
        advanceUntilIdle()

        assertFalse(viewModel.uiState.isLoading)
        assertNull(viewModel.uiState.error)
        assertTrue(viewModel.uiState.locations.isEmpty())
        assertTrue(viewModel.uiState.isEmpty)
    }

    @Test
    fun open_detail_success_sets_weather_detail() = runTest {
        val repository = FakeWeatherRepository().apply {
            queuedSearchResults.add(ApiResult.Success(listOf(vladivostok)))
            queuedWeatherResults.add(ApiResult.Success(detail))
        }
        val viewModel = WeatherViewModel(repository)

        viewModel.onEvent(WeatherEvent.SearchLocation("vla"))
        advanceUntilIdle()

        viewModel.onEvent(WeatherEvent.OpenDetail(vladivostok.id))
        advanceUntilIdle()

        assertFalse(viewModel.uiState.isLoadingDetail)
        assertNull(viewModel.uiState.errorDetail)
        assertEquals(vladivostok.id, viewModel.uiState.selectedLocation?.id)
        assertNotNull(viewModel.uiState.weatherDetail)
        assertEquals("Vladivostok", viewModel.uiState.weatherDetail?.location?.name)
        assertNotNull(viewModel.uiState.weatherDetail)
        assertEquals(6.0, viewModel.uiState.weatherDetail!!.current.temperature, 0.0)
    }

    @Test
    fun short_query_does_not_call_repository() = runTest {
        val repository = FakeWeatherRepository()
        val viewModel = WeatherViewModel(repository)

        viewModel.onEvent(WeatherEvent.SearchLocation("v"))
        advanceUntilIdle()

        assertEquals(0, repository.searchCalls)
        assertEquals("v", viewModel.uiState.searchQuery)
        assertTrue(viewModel.uiState.locations.isEmpty())
        assertFalse(viewModel.uiState.isLoading)
        assertFalse(viewModel.uiState.isEmpty)
    }
}