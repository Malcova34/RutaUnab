package com.rutaunab.app.presentation.screens.driver

import com.rutaunab.app.domain.model.User

data class DriverProfileUiState(
    val isLoading: Boolean = false,
    val driver: User? = null,
    val tripsToday: Int = 12,
    val hoursActive: String = "6.5h",
    val passengersToday: Int = 342,
    val schedule: List<ScheduleShift> = emptyList(),
    val recentTrips: List<Trip> = emptyList(),
    val errorMessage: String? = null
)

data class ScheduleShift(
    val time: String,
    val route: String,
    val status: ShiftStatus
)

enum class ShiftStatus {
    COMPLETED,
    IN_PROGRESS,
    PENDING
}

data class Trip(
    val route: String,
    val time: String,
    val passengers: Int,
    val from: String,
    val to: String
)

