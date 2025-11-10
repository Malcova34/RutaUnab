package com.rutaunab.app.data.repository

import com.rutaunab.app.data.firebase.firestore.FirestoreDataSource
import com.rutaunab.app.data.firebase.firestore.mapper.StopMapper
import com.rutaunab.app.domain.model.Location
import com.rutaunab.app.domain.model.Stop
import com.rutaunab.app.domain.repository.StopRepository
import com.rutaunab.app.domain.util.Result

class StopRepositoryImpl(
    private val firestore: FirestoreDataSource
) : StopRepository {

    override suspend fun getStopsByRoute(routeId: String): Result<List<Stop>> {
        return try {
            val stopDTOs = firestore.getStopsByRoute(routeId)
            val stops = stopDTOs.map { StopMapper.toDomain(it) }
            Result.Success(stops)
        } catch (e: Exception) {
            Result.Error(e, "Error al obtener paraderos: ${e.message}")
        }
    }

    override suspend fun getAllStops(): Result<List<Stop>> {
        return try {
            val stopDTOs = firestore.getAllStops()
            val stops = stopDTOs.map { StopMapper.toDomain(it) }
            Result.Success(stops)
        } catch (e: Exception) {
            Result.Error(e, "Error al obtener todos los paraderos: ${e.message}")
        }
    }

    override suspend fun getNearestStop(location: Location): Result<Stop> {
        return try {
            val stopDTOs = firestore.getAllStops()
            val stops = stopDTOs.map { StopMapper.toDomain(it) }
            
            val nearestStop = stops.minByOrNull { stop ->
                location.distanceTo(stop.location)
            } ?: throw Exception("No se encontraron paraderos")
            
            Result.Success(nearestStop)
        } catch (e: Exception) {
            Result.Error(e, "Error al obtener paradero más cercano: ${e.message}")
        }
    }

    override suspend fun calculateDistanceToStop(
        currentLocation: Location,
        stopId: String
    ): Result<Double> {
        return try {
            val stopDTOs = firestore.getAllStops()
            val stopDTO = stopDTOs.find { it.id == stopId }
                ?: throw Exception("Paradero no encontrado")
            
            val stop = StopMapper.toDomain(stopDTO)
            val distance = currentLocation.distanceTo(stop.location)
            
            Result.Success(distance)
        } catch (e: Exception) {
            Result.Error(e, "Error al calcular distancia: ${e.message}")
        }
    }
}

