package com.rutaunab.app.domain.repository

import com.rutaunab.app.domain.model.Route
import com.rutaunab.app.domain.util.Result

interface RouteRepository {
    
    /**
     * Obtener todas las rutas
     */
    suspend fun getAllRoutes(): Result<List<Route>>
    
    /**
     * Obtener detalles de una ruta por ID
     */
    suspend fun getRouteById(routeId: String): Result<Route>
    
    /**
     * Obtener buses activos en una ruta
     */
    suspend fun getActiveBusesForRoute(routeId: String): Result<List<String>>
}

