package com.rutaunab.app.presentation.screens.main.route

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RoutesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RoutesUiState())
    val uiState: StateFlow<RoutesUiState> = _uiState.asStateFlow()

    init {
        loadRoutes()
    }

    private fun loadRoutes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // TODO: En el futuro, esto debería venir de un Use Case que consulte la API
            // Por ahora, usamos datos mock
            val mockRoutes = listOf(
                RouteInfo(
                    id = 1,
                    name = "Ruta 1",
                    destination = "Metro República",
                    stops = 8,
                    frequency = "15 min",
                    status = "En servicio",
                    color = RouteColor.BLUE
                ),
                RouteInfo(
                    id = 2,
                    name = "Ruta 2",
                    destination = "Providencia",
                    stops = 12,
                    frequency = "20 min",
                    status = "En servicio",
                    color = RouteColor.GREEN
                )
            )

            _uiState.update {
                it.copy(
                    isLoading = false,
                    routes = mockRoutes
                )
            }
        }
    }

    fun onRouteClick(routeId: Int) {
        // TODO: En el futuro, se puede navegar a una pantalla de detalle de ruta
        // O actualizar el estado para mostrar información específica
    }
}

