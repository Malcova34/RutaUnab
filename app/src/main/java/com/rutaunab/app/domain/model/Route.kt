package com.rutaunab.app.domain.model

data class Route(
    val id: String,
    val name: String,
    val description: String? = null,
    val color: String = "#FEA604",  // Color para mostrar en mapa
    val stops: List<Stop> = emptyList(),
    val activeBusIds: List<String> = emptyList(),
    val isActive: Boolean = true,
    val estimatedDuration: Int? = null,  // Duración estimada en minutos
    val frequency: Int? = null  // Frecuencia en minutos
)

