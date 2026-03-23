package com.example.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.app.data.model.Location

@Entity(tableName = "favorite_locations")
data class FavoriteLocationEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String,
    val region: String?,
    val timezone: String
)

fun FavoriteLocationEntity.toLocation(): Location {
    return Location(
        id = id,
        name = name,
        latitude = latitude,
        longitude = longitude,
        country = country,
        region = region,
        timezone = timezone
    )
}

fun Location.toEntity(): FavoriteLocationEntity {
    return FavoriteLocationEntity(
        id = id,
        name = name,
        latitude = latitude,
        longitude = longitude,
        country = country,
        region = region,
        timezone = timezone
    )
}