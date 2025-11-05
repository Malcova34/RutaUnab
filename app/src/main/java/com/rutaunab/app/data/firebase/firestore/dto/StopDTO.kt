package com.rutaunab.app.data.firebase.firestore.dto

import com.google.firebase.firestore.PropertyName

data class StopDTO(
    @PropertyName("id") val id: String = "",
    @PropertyName("name") val name: String = "",
    @PropertyName("description") val description: String? = null,
    @PropertyName("location") val location: LocationDTO = LocationDTO(),
    @PropertyName("routeIds") val routeIds: List<String> = emptyList(),
    @PropertyName("order") val order: Int = 0,
    @PropertyName("estimatedWaitTime") val estimatedWaitTime: Int? = null
)

