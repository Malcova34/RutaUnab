package com.rutaunab.app.data.api.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO para parsear el JSON de la API de buses
 * La API devuelve un array de objetos JSON con campos en camelCase
 * 
 * Ejemplo de respuesta real:
 * [
 *   {
 *     "id": 191056,
 *     "placa": "RUTA02",
 *     "ninterno": "Servicio Gps Independiente",
 *     "lat": 7.1106,
 *     "lng": -73.1094,
 *     "estadoIgnicion": false,
 *     "evento": "Vehiculo Estacionado",
 *     "fhEvento": "2025-11-03T00:28:58",
 *     "sentido": 0,
 *     "tipo": "Bus",
 *     "cliente": "Rutas Unab",
 *     ...
 *   }
 * ]
 */
data class BusApiDTO(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("placa")
    val placa: String? = null,

    @SerializedName("lat")
    val lat: Double? = null,

    @SerializedName("lng")
    val lng: Double? = null,

    @SerializedName("estadoIgnicion")
    val estadoIgnicion: Boolean? = null,

    @SerializedName("evento")
    val nombreEvento: String? = null,

    @SerializedName("fhEvento")
    val fhEvento: String? = null,

    @SerializedName("tipo")
    val tipoVehiculo: String? = null,

    @SerializedName("sentido")
    val sentido: Int? = null,

    @SerializedName("cliente")
    val nombreCliente: String? = null,

    @SerializedName("ninterno")
    val nombreInterno: String? = null,

    @SerializedName("sinCoordenadas")
    val sinCoordenadas: Boolean? = null
)

