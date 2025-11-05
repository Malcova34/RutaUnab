package com.rutaunab.app.presentation.screens.main.home

import com.rutaunab.app.domain.model.User
import com.rutaunab.app.domain.model.Bus

data class HomeUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val activeRoutes: List<RouteInfo> = emptyList(),
    val activeRoutesCount: Int = 0,
    val totalStops: Int = 0,
    val errorMessage: String? = null,
    val showMapPlaceholder: Boolean = true // Cambiar a false cuando se implemente Google Maps
)

data class RouteInfo(
    val id: String,
    val name: String,
    val status: String,
    val busId: String? = null
)

