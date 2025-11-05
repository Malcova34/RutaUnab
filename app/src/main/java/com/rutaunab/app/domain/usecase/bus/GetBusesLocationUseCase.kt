package com.rutaunab.app.domain.usecase.bus

import com.rutaunab.app.domain.model.Bus
import com.rutaunab.app.domain.repository.BusRepository
import com.rutaunab.app.domain.util.Result

/**
 * Use case para obtener la ubicación de todos los buses en tiempo real
 */
class GetBusesLocationUseCase(
    private val busRepository: BusRepository
) {
    suspend operator fun invoke(): Result<List<Bus>> {
        return busRepository.getBusesLocation()
    }
}

