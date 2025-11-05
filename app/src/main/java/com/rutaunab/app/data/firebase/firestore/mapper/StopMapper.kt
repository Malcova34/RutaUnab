package com.rutaunab.app.data.firebase.firestore.mapper

import com.rutaunab.app.data.firebase.firestore.dto.LocationDTO
import com.rutaunab.app.data.firebase.firestore.dto.StopDTO
import com.rutaunab.app.domain.model.Location
import com.rutaunab.app.domain.model.Stop

object StopMapper {
    
    fun toDomain(dto: StopDTO): Stop {
        return Stop(
            id = dto.id,
            name = dto.name,
            description = dto.description,
            location = dto.location.toDomain(),
            routeIds = dto.routeIds,
            order = dto.order,
            estimatedWaitTime = dto.estimatedWaitTime
        )
    }
    
    fun toDTO(stop: Stop): StopDTO {
        return StopDTO(
            id = stop.id,
            name = stop.name,
            description = stop.description,
            location = stop.location.toDTO(),
            routeIds = stop.routeIds,
            order = stop.order,
            estimatedWaitTime = stop.estimatedWaitTime
        )
    }
    
    // Extension functions para convertir Location
    private fun LocationDTO.toDomain(): Location {
        return Location(
            latitude = latitude,
            longitude = longitude,
            timestamp = System.currentTimeMillis(),
            speed = null,
            bearing = null
        )
    }
    
    private fun Location.toDTO(): LocationDTO {
        return LocationDTO(
            latitude = latitude,
            longitude = longitude
        )
    }
}

