package com.rutaunab.app.presentation.screens.driver

import com.rutaunab.app.domain.model.ScanStatus
import com.rutaunab.app.domain.model.User

data class DriverQRScannerUiState(
    val isLoading: Boolean = false,
    val isScanning: Boolean = false,
    val currentDriver: User? = null,
    val recentScans: List<ScannedStudent> = emptyList(),
    val successScansToday: Int = 0,
    val rejectedScansToday: Int = 0,
    val scanResult: ScanResult? = null
)

data class ScannedStudent(
    val name: String,
    val studentId: String,
    val time: String,
    val status: ScanStatus
)
