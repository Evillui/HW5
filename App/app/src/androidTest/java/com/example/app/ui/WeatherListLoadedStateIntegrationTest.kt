package com.example.app.ui

import androidx.activity.ComponentActivity

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.app.data.model.Location
import com.example.app.ui.screens.WeatherListScreen
import com.example.app.ui.theme.WeatherAppTheme
import com.example.app.viewmodel.WeatherUiState
import org.junit.Rule
import org.junit.Test

class WeatherListLoadedStateIntegrationTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun loaded_state_shows_found_location() {
        val vladivostok = Location(
            id = 1,
            name = "Vladivostok",
            latitude = 43.1155,
            longitude = 131.8855,
            country = "Russia",
            region = "Primorsky Krai",
            timezone = "Asia/Vladivostok"
        )

        val uiState = WeatherUiState(
            isLoading = false,
            locations = listOf(vladivostok),
            favorites = emptyList(),
            favoriteIds = emptySet(),
            error = null,
            searchQuery = "vla",
            isEmpty = false,
            isLoadingDetail = false,
            weatherDetail = null,
            errorDetail = null,
            selectedLocation = null
        )

        composeRule.setContent {
            WeatherAppTheme {
                WeatherListScreen(
                    uiState = uiState,
                    onEvent = {},
                    onLocationClick = {},
                    onFavoritesClick = {}
                )
            }
        }

        composeRule.onNodeWithText("Vladivostok").assertExists()
        composeRule.onNodeWithText("Primorsky Krai").assertExists()
    }
}