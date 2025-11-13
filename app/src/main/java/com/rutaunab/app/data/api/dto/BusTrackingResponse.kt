package com.rutaunab.app.data.api.dto

import com.google.gson.annotations.SerializedName


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

