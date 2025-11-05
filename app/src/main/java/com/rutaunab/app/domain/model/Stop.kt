package com.rutaunab.app.domain.model

data class Stop(
    val id: String,
    val name: String,
    val description: String? = null,
    val location: Location,
    val routeIds: List<String> = emptyList(),  // Rutas que pasan por este paradero
    val order: Int = 0,  // Orden en la ruta
    val estimatedWaitTime: Int? = null  // Tiempo de espera estimado en minutos
)

