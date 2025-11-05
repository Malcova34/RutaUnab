package com.rutaunab.app.domain.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val studentId: String = "",      // idUnab en Firestore
    val career: String = "",         // carrera en Firestore
    val userType: UserType,          // role en Firestore
    val driverInfo: DriverInfo? = null,  // Opcional, solo para conductores
    val profileImageUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()  // createdAd en Firestore
)

