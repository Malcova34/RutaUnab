package com.rutaunab.app.domain.repository

import com.rutaunab.app.domain.model.Bus
import com.rutaunab.app.domain.util.Result

/**
 * Repositorio para acceder a los datos de buses en tiempo real
 */
interface BusRepository {
    
    /**
     * Obtiene la lista de todos los buses activos con su ubicación actual
     */
    suspend fun getBusesLocation(): Result<List<Bus>>
}
