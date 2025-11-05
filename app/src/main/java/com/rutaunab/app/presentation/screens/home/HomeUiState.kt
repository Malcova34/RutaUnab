package com.rutaunab.app.presentation.screens.home

import com.rutaunab.app.domain.model.User

data class HomeUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val activeRoutes: List<RouteInfo> = emptyList(),
    val activeRoutesCount: Int = 0,
    val totalStops: Int = 0,
    val errorMessage: String? = null,
    val showMapPlaceholder: Boolean = false // Google Maps ya está implementado
)

data class RouteInfo(
    val id: String,
    val name: String,
    val status: String,
    val busId: String? = null
)

