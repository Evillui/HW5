package com.example.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.app.ui.components.ErrorState
import com.example.app.ui.components.LoadingState
import com.example.app.ui.components.LocationItem
import com.example.app.viewmodel.WeatherEvent
import com.example.app.viewmodel.WeatherUiState
import androidx.compose.ui.platform.testTag
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherListScreen(
    uiState: WeatherUiState,
    onEvent: (WeatherEvent) -> Unit,
    onLocationClick: (com.example.app.data.model.Location) -> Unit,
    onFavoritesClick: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Weather App",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                actions = {
                    IconButton(
                        onClick = { onEvent(WeatherEvent.RefreshWeather) },
                        enabled = uiState.searchQuery.length >= 2
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }

                    IconButton(onClick = onFavoritesClick) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = "Favorites",
                            tint = if (uiState.favorites.isNotEmpty()) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { onEvent(WeatherEvent.SearchLocation(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("search_field"),
                placeholder = { Text("Search city...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { onEvent(WeatherEvent.ClearSearch) }
                        ) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = MaterialTheme.shapes.large
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isLoading -> {
                        LoadingState()
                    }

                    uiState.error != null -> {
                        ErrorState(
                            message = uiState.error,
                            onRetry = { onEvent(WeatherEvent.RefreshWeather) }
                        )
                    }

                    uiState.isEmpty -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "No locations found",
                                style = MaterialTheme.typography.bodyLarge
                            )

                            if (uiState.searchQuery.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { onEvent(WeatherEvent.ClearSearch) }
                                ) {
                                    Text("Clear search")
                                }
                            }
                        }
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                horizontal = 16.dp,
                                vertical = 8.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(uiState.locations) { location ->
                                LocationItem(
                                    location = location,
                                    isFavorite = uiState.favoriteIds.contains(location.id),
                                    onClick = { onLocationClick(location) },
                                    onFavoriteClick = {
                                        onEvent(WeatherEvent.ToggleFavorite(location))
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}