package com.rutaunab.app.presentation.screens.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.rutaunab.app.data.firebase.auth.FirebaseAuthDataSource
import com.rutaunab.app.data.firebase.firestore.FirestoreDataSource
import com.rutaunab.app.data.location.LocationService
import com.rutaunab.app.data.repository.AuthRepositoryImpl
import com.rutaunab.app.data.repository.StopRepositoryImpl
import com.rutaunab.app.domain.usecase.auth.GetCurrentUserUseCase
import com.rutaunab.app.domain.usecase.stop.GetAllStopsUseCase
import com.rutaunab.app.domain.util.Result
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val context: Context? = null,
    private val getCurrentUserUseCase: GetCurrentUserUseCase = GetCurrentUserUseCase(
        AuthRepositoryImpl(
            FirebaseAuthDataSource(),
            FirestoreDataSource()
        )
    )
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var locationJob: Job? = null

    private val locationService: LocationService? by lazy {
        context?.let { LocationService.getInstance(it) }
    }

    init {
        loadUserData()
        loadMockData() // Datos de prueba hasta que se implementen los Use Cases reales
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

    // TODO: Reemplazar con Use Cases reales cuando estén implementados
    private fun loadMockData() {
        val mockRoutes = listOf(
            RouteInfo(id = "1", name = "Ruta 1", status = "En servicio"),
            RouteInfo(id = "2", name = "Ruta 2", status = "En servicio"),
            RouteInfo(id = "3", name = "Ruta 3", status = "Fuera de servicio")
        )
        
        _uiState.update {
            it.copy(
                activeRoutes = mockRoutes.filter { route -> route.status == "En servicio" },
                activeRoutesCount = 3,
                totalStops = 12
            )
        }
    }

    fun refreshData() {
        loadUserData()
        loadMockData()
    }

    fun requestLocationPermission() {
        // Forzar la inicialización de ubicación cuando se conceden permisos
        observeUserLocation()
        // Intentar obtener la ubicación actual inmediatamente
        viewModelScope.launch {
            locationService?.getLastLocation()?.let { location ->
                _uiState.update { it.copy(userLocation = location) }
            }
        }
    }

    fun onLocationPermissionDenied() {
        // Usar ubicación por defecto cuando se deniegan permisos
        _uiState.update {
            it.copy(userLocation = LatLng(7.119444, -73.120833)) // Bucaramanga centro
        }
    }

    /**
     * Observa la ubicación del usuario en tiempo real
     */
    private fun observeUserLocation() {
        locationService?.let { service ->
            if (service.hasLocationPermission()) {
                locationJob = viewModelScope.launch {
                    try {
                        service.observeLocation().collect { location ->
                            _uiState.update { it.copy(userLocation = location) }
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("HomeViewModel", "Error observing location: ${e.message}")
                    }
                }
            } else {
                // Si no hay permisos, intentar obtener última ubicación conocida
                viewModelScope.launch {
                    val lastLocation = service.getLastLocation()
                    if (lastLocation != null) {
                        _uiState.update { it.copy(userLocation = lastLocation) }
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        locationJob?.cancel()
    }
}

