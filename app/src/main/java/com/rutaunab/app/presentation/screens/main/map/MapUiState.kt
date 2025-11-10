package com.rutaunab.app.presentation.screens.main.map

import com.google.android.gms.maps.model.LatLng
import com.rutaunab.app.domain.model.EstadoBus

data class MapUiState(
    val isLoading: Boolean = false,
    val selectedRoute: String? = "Ruta 1",
    val routes: List<RouteFilter> = emptyList(),
    val activeBuses: List<BusLocation> = emptyList(),
    val userLocation: LatLng? = null,
    val shouldCenterOnUser: Boolean = false,
    val errorMessage: String? = null
)

data class RouteFilter(
    val id: String,
    val name: String,
    val color: RouteMapColor,
    val isActive: Boolean = true
)

data class BusLocation(
    val route: String,
    val location: String,
    val latLng: LatLng,
    val placa: String = "",
    val estado: EstadoBus = EstadoBus.DESCONOCIDO
)

enum class RouteMapColor {
    BLUE,
    GREEN,
    RED,
    YELLOW
}

