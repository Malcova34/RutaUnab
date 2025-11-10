package com.rutaunab.app.domain.repository

import com.rutaunab.app.domain.model.Location
import com.rutaunab.app.domain.model.Stop
import com.rutaunab.app.domain.util.Result

interface StopRepository {

    /**
     * Obtener paraderos por ruta
     */
    suspend fun getStopsByRoute(routeId: String): Result<List<Stop>>

    /**
     * Obtener todos los paraderos
     */
    suspend fun getAllStops(): Result<List<Stop>>

    /**
     * Obtener el paradero más cercano a una ubicación
     */
    suspend fun getNearestStop(location: Location): Result<Stop>

    /**
     * Calcular distancia a un paradero
     */
    suspend fun calculateDistanceToStop(
        currentLocation: Location,
        stopId: String
    ): Result<Double>
}

