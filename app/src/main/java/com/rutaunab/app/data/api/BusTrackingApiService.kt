package com.rutaunab.app.data.api

import com.rutaunab.app.data.api.dto.BusApiDTO
import retrofit2.Response
import retrofit2.http.GET

/**
 * Servicio API para obtener la ubicación de los buses en tiempo real
 */
interface BusTrackingApiService {
    

    @GET("api/rep-actual/ultimo-avl/d6871041==")
    suspend fun getBusesLocation(): Response<List<BusApiDTO>>
}

