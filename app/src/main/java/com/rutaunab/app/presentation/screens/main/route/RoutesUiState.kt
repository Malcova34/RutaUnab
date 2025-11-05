package com.rutaunab.app.presentation.screens.main.route

data class RoutesUiState(
    val isLoading: Boolean = false,
    val routes: List<RouteInfo> = emptyList(),
    val errorMessage: String? = null
)

data class RouteInfo(
    val id: Int,
    val name: String,
    val destination: String,
    val stops: Int,
    val frequency: String,
    val status: String,
    val color: RouteColor
)

enum class RouteColor {
    BLUE,
    GREEN,
    RED,
    YELLOW
}

