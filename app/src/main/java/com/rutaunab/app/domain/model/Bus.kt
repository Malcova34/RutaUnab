package com.rutaunab.app.domain.model

import com.google.android.gms.maps.model.LatLng

/**
 * Modelo de dominio para un bus en tiempo real
 */
data class Bus(
    val id: Int,
    val placa: String,
    val latLng: LatLng,
    val estado: EstadoBus,
    val ultimaActualizacion: String,
    val tipoVehiculo: String,
    val sentido: Int, // Dirección en grados (0-360)
    val descripcion: String
)

/**
 * Estados posibles de un bus
 */
enum class EstadoBus {
    EN_MOVIMIENTO,
    ESTACIONADO,
    SIN_SEÑAL,
    DESCONOCIDO
}
