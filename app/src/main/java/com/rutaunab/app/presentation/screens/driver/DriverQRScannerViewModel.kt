package com.rutaunab.app.presentation.screens.driver

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rutaunab.app.data.firebase.auth.FirebaseAuthDataSource
import com.rutaunab.app.data.firebase.firestore.FirestoreDataSource
import com.rutaunab.app.data.repository.AuthRepositoryImpl
import com.rutaunab.app.data.repository.QRScanRepository
import com.rutaunab.app.domain.model.QRScan
import com.rutaunab.app.domain.model.ScanStatus
import com.rutaunab.app.domain.usecase.auth.GetCurrentUserUseCase
import com.rutaunab.app.domain.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DriverQRScannerViewModel(
    private val context: Context? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(DriverQRScannerUiState())
    val uiState: StateFlow<DriverQRScannerUiState> = _uiState.asStateFlow()

    private val getCurrentUserUseCase = GetCurrentUserUseCase(
        AuthRepositoryImpl(
            FirebaseAuthDataSource(),
            FirestoreDataSource()
        )
    )
    
    private val qrScanRepository = QRScanRepository()

    init {
        loadDriverData()
        loadRecentScans()
    }

    private fun loadDriverData() {
        viewModelScope.launch {
            when (val result = getCurrentUserUseCase()) {
                is Result.Success -> {
                    val driver = result.data
                    _uiState.update { it.copy(currentDriver = driver) }
                }
                else -> {}
            }
        }
    }

    private fun loadRecentScans() {
        viewModelScope.launch {
            val driverId = _uiState.value.currentDriver?.id ?: return@launch
            
            when (val result = qrScanRepository.getDriverScans(driverId, 10)) {
                is Result.Success -> {
                    val scans = result.data.map { scan ->
                        ScannedStudent(
                            name = scan.studentName,
                            studentId = scan.studentId,
                            time = formatTime(scan.timestamp.toDate()),
                            status = if (scan.status == ScanStatus.SUCCESS) 
                                ScanStatus.SUCCESS else ScanStatus.INVALID_QR
                        )
                    }
                    _uiState.update { it.copy(recentScans = scans) }
                }
                else -> {}
            }
            
            // Cargar contadores de hoy
            val successCount = qrScanRepository.getTodayScansCount(driverId)
            _uiState.update { 
                it.copy(
                    successScansToday = successCount,
                    rejectedScansToday = 0
                )
            }
        }
    }

    /**
     * Procesa un código QR escaneado
     */
    fun onQRScanned(qrContent: String) {
        val driver = _uiState.value.currentDriver
        if (driver == null) {
            _uiState.update { 
                it.copy(
                    scanResult = ScanResult.Error("Error: conductor no identificado")
                )
            }
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isScanning = true) }
            
            val result = qrScanRepository.validateAndRegisterScan(
                qrContent = qrContent,
                driverId = driver.id,
                driverName = driver.name,
                busId = driver.driverInfo?.assignedBusId ?: "N/A"
            )
            
            when (result) {
                is Result.Success -> {
                    val scan = result.data
                    _uiState.update { 
                        it.copy(
                            isScanning = false,
                            scanResult = ScanResult.Success(scan.studentName),
                            successScansToday = it.successScansToday + 1
                        )
                    }
                    // Recargar la lista de escaneos
                    loadRecentScans()
                }
                is Result.Error -> {
                    _uiState.update { 
                        it.copy(
                            isScanning = false,
                            scanResult = ScanResult.Error(result.message ?: "Error desconocido"),
                            rejectedScansToday = it.rejectedScansToday + 1
                        )
                    }
                }
                else -> {
                    _uiState.update { it.copy(isScanning = false) }
                }
            }
        }
    }

    /**
     * Limpia el resultado del escaneo
     */
    fun clearScanResult() {
        _uiState.update { it.copy(scanResult = null) }
    }

    private fun formatTime(date: Date): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(date)
    }
}

sealed class ScanResult {
    data class Success(val studentName: String) : ScanResult()
    data class Error(val message: String) : ScanResult()
}
