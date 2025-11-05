package com.rutaunab.app.data.api.mapper

import com.google.android.gms.maps.model.LatLng
import com.rutaunab.app.data.api.dto.BusApiDTO
import com.rutaunab.app.domain.model.Bus
import com.rutaunab.app.domain.model.EstadoBus

/**
 * Mapper para convertir BusApiDTO a modelo de dominio Bus
 */
object BusMapper {
    
    fun toDomain(dto: BusApiDTO): Bus? {
        // Validar que tenga datos mínimos necesarios
        if (dto.id == null || dto.placa == null || dto.lat == null || dto.lng == null) {
            return null
        }
        
        // Si el bus no tiene coordenadas válidas, no lo incluimos
        if (dto.sinCoordenadas == true || dto.lat == 0.0 || dto.lng == 0.0) {
            return null
        }
        
        return Bus(
            id = dto.id!!,
            placa = dto.placa!!,
            latLng = LatLng(dto.lat!!, dto.lng!!),
            estado = mapEstado(dto.estadoIgnicion, dto.nombreEvento),
            ultimaActualizacion = dto.fhEvento ?: "",
            tipoVehiculo = dto.tipoVehiculo ?: "Bus",
            sentido = dto.sentido ?: 0,
            descripcion = dto.nombreInterno ?: dto.placa!!
        )
    }
    
    fun toDomainList(dtos: List<BusApiDTO>?): List<Bus> {
        return dtos?.mapNotNull { toDomain(it) } ?: emptyList()
    }
    
    private fun mapEstado(ignicion: Boolean?, nombreEvento: String?): EstadoBus {
        return when {
            ignicion == true -> EstadoBus.EN_MOVIMIENTO
            ignicion == false -> EstadoBus.ESTACIONADO
            nombreEvento?.contains("Movimiento", ignoreCase = true) == true -> EstadoBus.EN_MOVIMIENTO
            nombreEvento?.contains("Estacionado", ignoreCase = true) == true -> EstadoBus.ESTACIONADO
            else -> EstadoBus.DESCONOCIDO
        }
    }
}

