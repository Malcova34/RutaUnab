package com.rutaunab.app.data.repository

import com.rutaunab.app.data.firebase.firestore.FirestoreDataSource
import com.rutaunab.app.data.firebase.firestore.mapper.RouteMapper
import com.rutaunab.app.data.firebase.firestore.mapper.StopMapper
import com.rutaunab.app.domain.model.Route
import com.rutaunab.app.domain.repository.RouteRepository
import com.rutaunab.app.domain.util.Result

class RouteRepositoryImpl(
    private val firestore: FirestoreDataSource
) : RouteRepository {

    override suspend fun getAllRoutes(): Result<List<Route>> {
        return try {
            val routeDTOs = firestore.getAllRoutes()
            val routes = routeDTOs.map { routeDTO ->
                val stopDTOs = firestore.getStopsByRoute(routeDTO.id)
                val stops = stopDTOs.map { StopMapper.toDomain(it) }
                RouteMapper.toDomain(routeDTO, stops)
            }
            Result.Success(routes)
        } catch (e: Exception) {
            Result.Error(e, "Error al obtener rutas: ${e.message}")
        }
    }

    override suspend fun getRouteById(routeId: String): Result<Route> {
        return try {
            val routeDTO = firestore.getRoute(routeId)
                ?: throw Exception("Ruta no encontrada")
            
            val stopDTOs = firestore.getStopsByRoute(routeId)
            val stops = stopDTOs.map { StopMapper.toDomain(it) }
            
            Result.Success(RouteMapper.toDomain(routeDTO, stops))
        } catch (e: Exception) {
            Result.Error(e, "Error al obtener ruta: ${e.message}")
        }
    }

    override suspend fun getActiveBusesForRoute(routeId: String): Result<List<String>> {
        return try {
            val routeDTO = firestore.getRoute(routeId)
                ?: throw Exception("Ruta no encontrada")
            
            Result.Success(routeDTO.activeBusIds)
        } catch (e: Exception) {
            Result.Error(e, "Error al obtener buses activos: ${e.message}")
        }
    }
}

