package com.rutaunab.app.data.firebase.firestore.mapper

import com.rutaunab.app.data.firebase.firestore.dto.RouteDTO
import com.rutaunab.app.domain.model.Route
import com.rutaunab.app.domain.model.Stop

object RouteMapper {
    
    fun toDomain(dto: RouteDTO, stops: List<Stop> = emptyList()): Route {
        return Route(
            id = dto.id,
            name = dto.name,
            description = dto.description,
            color = dto.color,
            stops = stops,
            activeBusIds = dto.activeBusIds,
            isActive = dto.isActive,
            estimatedDuration = dto.estimatedDuration,
            frequency = dto.frequency
        )
    }
    
    fun toDTO(route: Route): RouteDTO {
        return RouteDTO(
            id = route.id,
            name = route.name,
            description = route.description,
            color = route.color,
            stopIds = route.stops.map { it.id },
            activeBusIds = route.activeBusIds,
            isActive = route.isActive,
            estimatedDuration = route.estimatedDuration,
            frequency = route.frequency
        )
    }
}

