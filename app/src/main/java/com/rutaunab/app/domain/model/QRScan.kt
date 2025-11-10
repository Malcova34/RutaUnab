package com.rutaunab.app.domain.model

import com.google.firebase.Timestamp

data class QRScan(
    val id: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val driverId: String = "",
    val driverName: String = "",
    val busId: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val qrData: String = "",
    val status: ScanStatus = ScanStatus.SUCCESS
)

enum class ScanStatus {
    SUCCESS,
    INVALID_QR,
    EXPIRED_QR,
    DUPLICATE,
    USER_NOT_FOUND,
    ERROR
}

