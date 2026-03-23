package com.example.app.fakes

import com.example.app.data.api.GeocodingApi
import com.example.app.data.model.Location
import com.example.app.data.model.LocationResponse

class FakeGeocodingApi(
    private val results: List<Location>
) : GeocodingApi {

    override suspend fun searchLocations(
        query: String,
        count: Int,
        language: String
    ): LocationResponse {
        return LocationResponse(results = results)
    }
}