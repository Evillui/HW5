package com.example.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoriteLocationDao {

    @Query("SELECT * FROM favorite_locations")
    suspend fun getAllFavorites(): List<FavoriteLocationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(location: FavoriteLocationEntity)

    @Query("DELETE FROM favorite_locations WHERE id = :id")
    suspend fun deleteFavoriteById(id: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_locations WHERE id = :id)")
    suspend fun isFavorite(id: Int): Boolean
}