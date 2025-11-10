package com.rutaunab.app.presentation.screens.main.profile

import com.rutaunab.app.domain.model.User

data class ProfileUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val totalTrips: Int = 0,
    val tripsThisMonth: Int = 0,
    val mostUsedRoute: String = "N/A",
    val tripsCount: Int = 0, // Mantener por compatibilidad
    val timeSaved: String = "0h",
    val favoriteRoutesCount: Int = 1,
    val recentActivity: List<ActivityItem> = emptyList(),
    val errorMessage: String? = null
)

data class ActivityItem(
    val routeName: String,
    val time: String,
    val location: String
)

