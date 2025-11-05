package com.rutaunab.app.data.repository

import com.rutaunab.app.data.api.BusTrackingDataSource
import com.rutaunab.app.data.api.mapper.BusMapper
import com.rutaunab.app.domain.model.Bus
import com.rutaunab.app.domain.repository.BusRepository
import com.rutaunab.app.domain.util.Result

/**
 * Implementación del repositorio de buses
 */
class BusRepositoryImpl(
    private val busTrackingDataSource: BusTrackingDataSource
) : BusRepository {
    
    override suspend fun getBusesLocation(): Result<List<Bus>> {
        return try {
            val busesDTO = busTrackingDataSource.getBusesLocation()
            
            if (!busesDTO.isNullOrEmpty()) {
                val buses = BusMapper.toDomainList(busesDTO)
                android.util.Log.d("BusRepository", "✅ ${buses.size} buses convertidos exitosamente")
                Result.Success(buses)
            } else {
                android.util.Log.w("BusRepository", "⚠️ Respuesta vacía o nula de la API")
                Result.Success(emptyList()) // Retornar lista vacía en lugar de error
            }
        } catch (e: Exception) {
            android.util.Log.e("BusRepository", "❌ Error: ${e.message}", e)
            Result.Error(Exception("Error al obtener ubicación de buses: ${e.message}"))
        }
    }
}
