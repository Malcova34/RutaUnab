package com.rutaunab.app.presentation.screens.driver

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rutaunab.app.data.firebase.auth.FirebaseAuthDataSource
import com.rutaunab.app.data.firebase.firestore.FirestoreDataSource
import com.rutaunab.app.data.local.SessionManager
import com.rutaunab.app.data.repository.AuthRepositoryImpl
import com.rutaunab.app.domain.usecase.auth.GetCurrentUserUseCase
import com.rutaunab.app.domain.usecase.auth.LogoutUseCase
import com.rutaunab.app.domain.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DriverProfileViewModel(
    private val context: Context? = null,
    // TODO: Inyectar con Hilt cuando esté configurado
    private val getCurrentUserUseCase: GetCurrentUserUseCase = GetCurrentUserUseCase(
        AuthRepositoryImpl(
            FirebaseAuthDataSource(),
            FirestoreDataSource()
        )
    ),
    private val logoutUseCase: LogoutUseCase = LogoutUseCase(
        AuthRepositoryImpl(
            FirebaseAuthDataSource(),
            FirestoreDataSource()
        )
    )
) : ViewModel() {

    private val _uiState = MutableStateFlow(DriverProfileUiState())
    val uiState: StateFlow<DriverProfileUiState> = _uiState.asStateFlow()
    
    private val sessionManager: SessionManager? by lazy {
        context?.let { SessionManager.getInstance(it) }
    }

    init {
        loadDriverData()
        loadMockSchedule()
        loadMockRecentTrips()
    }

    private fun loadDriverData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            when (val result = getCurrentUserUseCase()) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            driver = result.data,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.exception.message
                        )
                    }
                }
                is Result.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun loadMockSchedule() {
        // TODO: Reemplazar con datos reales
        val mockSchedule = listOf(
            ScheduleShift("07:00 - 09:00", "Ruta 1", ShiftStatus.COMPLETED),
            ScheduleShift("09:30 - 11:30", "Ruta 2", ShiftStatus.COMPLETED),
            ScheduleShift("12:00 - 14:00", "Ruta 1", ShiftStatus.IN_PROGRESS),
            ScheduleShift("14:30 - 16:30", "Ruta 2", ShiftStatus.PENDING)
        )
        
        _uiState.update { it.copy(schedule = mockSchedule) }
    }

    private fun loadMockRecentTrips() {
        // TODO: Reemplazar con datos reales
        val mockTrips = listOf(
            Trip("Ruta 1", "12:30", 28, "Campus Bellavista", "Metro República"),
            Trip("Ruta 1", "11:15", 32, "Metro República", "Campus Bellavista"),
            Trip("Ruta 2", "10:00", 25, "Providencia", "Campus Casona")
        )
        
        _uiState.update { it.copy(recentTrips = mockTrips) }
    }

    fun refreshData() {
        loadDriverData()
        loadMockSchedule()
        loadMockRecentTrips()
    }
    
    fun logout(onLogoutComplete: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            when (val result = logoutUseCase()) {
                is Result.Success -> {
                    // Limpiar sesión guardada
                    sessionManager?.clearSession()
                    
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                    onLogoutComplete()
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.exception.message ?: "Error al cerrar sesión"
                        )
                    }
                }
                is Result.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }
}

