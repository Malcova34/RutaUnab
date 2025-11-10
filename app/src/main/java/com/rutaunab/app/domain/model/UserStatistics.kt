package com.rutaunab.app.domain.model

data class UserStatistics(
    val totalTrips: Int = 0,
    val tripsThisMonth: Int = 0,
    val mostUsedRoute: String = "Ruta 1",
    val routeUsageCounts: Map<String, Int> = emptyMap(),
    val lastTripDate: String? = null,
    val averageTripsPerWeek: Double = 0.0
)

data class RouteUsage(
    val routeName: String,
    val usageCount: Int,
    val percentage: Int
)

