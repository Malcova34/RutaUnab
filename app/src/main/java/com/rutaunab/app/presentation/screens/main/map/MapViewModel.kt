package com.rutaunab.app.presentation.screens.main.map

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.rutaunab.app.data.api.BusTrackingDataSource
import com.rutaunab.app.data.location.LocationService
import com.rutaunab.app.data.repository.BusRepositoryImpl
import com.rutaunab.app.domain.model.EstadoBus
import com.rutaunab.app.domain.usecase.bus.GetBusesLocationUseCase
import com.rutaunab.app.domain.util.Result
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MapViewModel(
    private val context: Context? = null,
    // TODO: Inyectar con Hilt cuando esté configurado
    private val getBusesLocationUseCase: GetBusesLocationUseCase = GetBusesLocationUseCase(
        BusRepositoryImpl(
            BusTrackingDataSource()
        )
    )
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()
    
    private var refreshJob: Job? = null
    private var locationJob: Job? = null
    
    private val locationService: LocationService? by lazy {
        context?.let { LocationService.getInstance(it) }
    }

    init {
        loadMapData()
        startAutoRefresh()
        // observeUserLocation() se llamará después de verificar permisos
    }

    private fun loadMapData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                // Obtener buses de la API real
                when (val result = getBusesLocationUseCase()) {
                    is Result.Success -> {
                        val buses = result.data
                        
                        if (buses.isEmpty()) {
                            // Si no hay buses, usar datos mock
                            loadMockData()
                        } else {
                            // Convertir buses a BusLocation para el mapa
                            val busLocations = buses.map { bus ->
                                BusLocation(
                                    route = determineRoute(bus.placa),
                                    location = "${bus.estado.name} - ${bus.descripcion}",
                                    latLng = bus.latLng,
                                    placa = bus.placa,
                                    estado = bus.estado
                                )
                            }
                            
                            // Crear filtros de rutas únicas
                            val routes = buses.map { it.placa }
                                .distinct()
                                .mapIndexed { index, placa ->
                                    RouteFilter(
                                        id = placa,
                                        name = determineRoute(placa),
                                        color = getColorForRoute(index),
                                        isActive = true
                                    )
                                }
                            
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    routes = routes,
                                    activeBuses = busLocations,
                                    userLocation = LatLng(7.119444, -73.120833), // Bucaramanga centro
                                    errorMessage = null
                                )
                            }
                        }
                    }
                    is Result.Error -> {
                        // Si hay error, usar datos mock como fallback
                        android.util.Log.e("MapViewModel", "Error al cargar buses: ${result.exception.message}")
                        loadMockData()
                    }
                    is Result.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("MapViewModel", "Excepción al cargar datos: ${e.message}", e)
                loadMockData()
            }
        }
    }
    
    private fun loadMockData() {
        android.util.Log.d("MapViewModel", "⚠️ Usando datos MOCK como fallback")
        
        val mockRoutes = listOf(
            RouteFilter(
                id = "1",
                name = "Ruta 1",
                color = RouteMapColor.BLUE,
                isActive = true
            ),
            RouteFilter(
                id = "2",
                name = "Ruta 2",
                color = RouteMapColor.GREEN,
                isActive = true
            )
        )

        // Ubicaciones mock cerca de Bucaramanga
        val mockBuses = listOf(
            BusLocation(
                route = "Ruta 1",
                location = "Demo - En servicio",
                latLng = LatLng(7.119444, -73.120833),
                placa = "RUTA1",
                estado = EstadoBus.EN_MOVIMIENTO
            ),
            BusLocation(
                route = "Ruta 2",
                location = "Demo - Estacionado",
                latLng = LatLng(7.125000, -73.115000),
                placa = "RUTA2",
                estado = EstadoBus.ESTACIONADO
            )
        )

        _uiState.update {
            it.copy(
                isLoading = false,
                routes = mockRoutes,
                activeBuses = mockBuses,
                userLocation = LatLng(7.119444, -73.120833),
                errorMessage = "Usando datos de demostración (API no disponible)"
            )
        }
    }
    
    private fun startAutoRefresh() {
        refreshJob = viewModelScope.launch {
            while (true) {
                delay(30000) // Actualizar cada 30 segundos
                loadMapData()
            }
        }
    }
    
    private fun determineRoute(placa: String): String {
        return when {
            placa.contains("RUTA1", ignoreCase = true) -> "Ruta 1"
            placa.contains("RUTA2", ignoreCase = true) || placa.contains("RUTA02", ignoreCase = true) -> "Ruta 2"
            else -> "Ruta $placa"
        }
    }
    
    private fun getColorForRoute(index: Int): RouteMapColor {
        return when (index % 4) {
            0 -> RouteMapColor.BLUE
            1 -> RouteMapColor.GREEN
            2 -> RouteMapColor.RED
            else -> RouteMapColor.YELLOW
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
                        android.util.Log.e("MapViewModel", "Error observing location: ${e.message}")
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
    
    fun onRouteFilterClick(routeName: String?) {
        _uiState.update { it.copy(selectedRoute = routeName) }
    }

    fun onCenterToUserLocation() {
        _uiState.value.userLocation?.let { location ->
            _uiState.update { it.copy(shouldCenterOnUser = true) }
        }
    }

    fun requestLocationPermission() {
        // Forzar la inicialización de ubicación cuando se conceden permisos
        observeUserLocation()
        // Intentar obtener la ubicación actual inmediatamente
        viewModelScope.launch {
            locationService?.getLastLocation()?.let { location ->
                _uiState.update { it.copy(userLocation = location, shouldCenterOnUser = true) }
            }
        }
    }

    fun onLocationPermissionDenied() {
        // Usar ubicación por defecto cuando se deniegan permisos
        _uiState.update {
            it.copy(userLocation = LatLng(7.119444, -73.120833)) // Bucaramanga centro
        }
    }
    
    fun onCameraMoved() {
        _uiState.update { it.copy(shouldCenterOnUser = false) }
    }
    
    fun refreshBuses() {
        loadMapData()
    }
    
    override fun onCleared() {
        super.onCleared()
        refreshJob?.cancel()
        locationJob?.cancel()
    }
}

