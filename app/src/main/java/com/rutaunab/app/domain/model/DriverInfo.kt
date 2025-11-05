package com.rutaunab.app.domain.model

data class DriverInfo(
    val licenseNumber: String,
    val licenseType: String,
    val assignedBusId: String? = null,
    val yearsOfExperience: Int = 0,
    val rating: Double = 0.0,
    val totalTrips: Int = 0
)

