package com.example.app.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.app.data.local.AppDatabase
import com.example.app.data.model.Location
import com.example.app.fakes.FakeGeocodingApi
import com.example.app.fakes.FakeWeatherApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WeatherRepositoryIntegrationTest {

    private lateinit var database: AppDatabase

    private val vladivostok = Location(
        id = 1,
        name = "Vladivostok",
        latitude = 43.1155,
        longitude = 131.8855,
        country = "Russia",
        region = "Primorsky Krai",
        timezone = "Asia/Vladivostok"
    )

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun toggleFavorite_thenGetFavorites_readsSavedDataFromRoom() = runBlocking {
        val repository = WeatherRepository(
            geocodingApi = FakeGeocodingApi(results = listOf(vladivostok)),
            weatherApi = FakeWeatherApi(),
            favoriteLocationDao = database.favoriteLocationDao()
        )

        repository.toggleFavorite(vladivostok)
        val favorites = repository.getFavorites()

        assertEquals(1, favorites.size)
        assertEquals("Vladivostok", favorites.first().name)
    }
}