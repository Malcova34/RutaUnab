package com.rutaunab.app.domain.usecase.stop

import com.rutaunab.app.domain.model.Stop
import com.rutaunab.app.domain.repository.StopRepository
import com.rutaunab.app.domain.util.Result

/**
 * Use case para obtener todos los paraderos
 */
class GetAllStopsUseCase(
    private val stopRepository: StopRepository
) {
    suspend operator fun invoke(): Result<List<Stop>> {
        return stopRepository.getAllStops()
    }
}