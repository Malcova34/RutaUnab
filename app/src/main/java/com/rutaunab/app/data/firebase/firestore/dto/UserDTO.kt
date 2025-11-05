package com.rutaunab.app.data.firebase.firestore.dto

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp

data class UserDTO(
    @PropertyName("id") val id: String = "",
    @PropertyName("fullName") val fullName: String = "",
    @PropertyName("email") val email: String = "",
    @PropertyName("idUnab") val idUnab: String = "",
    @PropertyName("carrera") val carrera: String = "",
    @PropertyName("role") val role: String = "usuario normal",
    @PropertyName("profileImageUrl") val profileImageUrl: String? = null,
    @ServerTimestamp
    @PropertyName("createdAd") val createdAd: Timestamp? = null,
    @PropertyName("driverInfo") val driverInfo: DriverInfoDTO? = null
)

data class DriverInfoDTO(
    @PropertyName("licenseNumber") val licenseNumber: String = "",
    @PropertyName("licenseType") val licenseType: String = "",
    @PropertyName("assignedBusId") val assignedBusId: String? = null,
    @PropertyName("yearsOfExperience") val yearsOfExperience: Int = 0,
    @PropertyName("rating") val rating: Double = 0.0,
    @PropertyName("totalTrips") val totalTrips: Int = 0
)

