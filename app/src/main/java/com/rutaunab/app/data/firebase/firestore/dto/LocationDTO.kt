package com.rutaunab.app.data.firebase.firestore.dto

import com.google.firebase.firestore.PropertyName

/**
 * DTO para representar una ubicación geográfica (usado en Stops)
 */
data class LocationDTO(
    @PropertyName("latitude") val latitude: Double = 0.0,
    @PropertyName("longitude") val longitude: Double = 0.0
)

