package com.rutaunab.app.data.firebase.firestore.dto

import com.google.firebase.firestore.PropertyName

data class RouteDTO(
    @PropertyName("id") val id: String = "",
    @PropertyName("name") val name: String = "",
    @PropertyName("description") val description: String? = null,
    @PropertyName("color") val color: String = "#FEA604",
    @PropertyName("stopIds") val stopIds: List<String> = emptyList(),
    @PropertyName("activeBusIds") val activeBusIds: List<String> = emptyList(),
    @PropertyName("isActive") val isActive: Boolean = true,
    @PropertyName("estimatedDuration") val estimatedDuration: Int? = null,
    @PropertyName("frequency") val frequency: Int? = null
)

