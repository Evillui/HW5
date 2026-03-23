package com.example.app.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoriteLocationDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: FavoriteLocationDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        dao = database.favoriteLocationDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertFavorite_andGetAllFavorites_returnsSavedItem() = runBlocking {
        val entity = FavoriteLocationEntity(
            id = 1,
            name = "Vladivostok",
            latitude = 43.1155,
            longitude = 131.8855,
            country = "Russia",
            region = "Primorsky Krai",
            timezone = "Asia/Vladivostok"
        )

        dao.insertFavorite(entity)
        val result = dao.getAllFavorites()

        assertEquals(1, result.size)
        assertEquals("Vladivostok", result.first().name)
    }
}