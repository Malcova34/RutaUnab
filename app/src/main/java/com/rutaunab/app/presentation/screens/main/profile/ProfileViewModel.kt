package com.rutaunab.app.presentation.screens.main.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rutaunab.app.data.firebase.auth.FirebaseAuthDataSource
import com.rutaunab.app.data.firebase.firestore.FirestoreDataSource
import com.rutaunab.app.data.repository.AuthRepositoryImpl
import com.rutaunab.app.data.repository.QRScanRepository
import com.rutaunab.app.domain.usecase.auth.GetCurrentUserUseCase
import com.rutaunab.app.domain.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    // TODO: Inyectar con Hilt cuando esté configurado
    private val getCurrentUserUseCase: GetCurrentUserUseCase = GetCurrentUserUseCase(
        AuthRepositoryImpl(
            FirebaseAuthDataSource(),
            FirestoreDataSource()
        )
    ),
    private val qrScanRepository: QRScanRepository = QRScanRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserData()
        loadStatistics()
        loadRecentActivity()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            when (val result = getCurrentUserUseCase()) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            user = result.data,
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

    private fun loadStatistics() {
        viewModelScope.launch {
            val userId = _uiState.value.user?.id ?: return@launch
            
            when (val result = qrScanRepository.getStudentStatistics(userId)) {
                is Result.Success -> {
                    val stats = result.data
                    _uiState.update { 
                        it.copy(
                            totalTrips = stats.totalTrips,
                            tripsThisMonth = stats.tripsThisMonth,
                            mostUsedRoute = stats.mostUsedRoute
                        )
                    }
                }
                else -> {
                    // Si hay error, usar valores por defecto
                    _uiState.update { 
                        it.copy(
                            totalTrips = 0,
                            tripsThisMonth = 0,
                            mostUsedRoute = "N/A"
                        )
                    }
                }
            }
        }
    }

    private fun loadRecentActivity() {
        viewModelScope.launch {
            val userId = _uiState.value.user?.id ?: return@launch
            
            when (val result = qrScanRepository.getStudentScans(userId, 10)) {
                is Result.Success -> {
                    val scans = result.data
                    val activities = scans.map { scan ->
                        val date = scan.timestamp.toDate()
                        val timeText = formatTime(date)
                        
                        ActivityItem(
                            routeName = scan.busId,
                            time = timeText,
                            location = "Viaje registrado"
                        )
                    }
                    
                    _uiState.update { it.copy(recentActivity = activities) }
                }
                else -> {
                    // Si hay error, usar datos mock
                    val mockActivity = listOf(
                        ActivityItem(
                            routeName = "Ruta 1",
                            time = "Hoy, 08:15",
                            location = "Metro República"
                        ),
                        ActivityItem(
                            routeName = "Ruta 2",
                            time = "Ayer, 18:30",
                            location = "Providencia"
                        )
                    )
                    
                    _uiState.update { it.copy(recentActivity = mockActivity) }
                }
            }
        }
    }
    
    private fun formatTime(date: java.util.Date): String {
        val now = java.util.Calendar.getInstance()
        val scanTime = java.util.Calendar.getInstance().apply { time = date }
        
        val daysDiff = ((now.timeInMillis - scanTime.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()
        
        val timeFormat = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        val time = timeFormat.format(date)
        
        return when {
            daysDiff == 0 -> "Hoy, $time"
            daysDiff == 1 -> "Ayer, $time"
            daysDiff < 7 -> "$daysDiff días atrás, $time"
            else -> {
                val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
                dateFormat.format(date)
            }
        }
    }

    fun refreshData() {
        loadUserData()
        loadStatistics()
        loadRecentActivity()
    }
}

