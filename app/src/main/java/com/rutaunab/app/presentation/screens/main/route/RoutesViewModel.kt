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
            

            val mockRoutes = listOf(
                RouteInfo(
                    id = 1,
                    name = "Ruta 1",
                    destination = "Casona Unab",
                    stops = 12,
                    frequency = "15 min",
                    status = "En servicio",
                    color = RouteColor.BLUE
                ),
                RouteInfo(
                    id = 2,
                    name = "Ruta 2",
                    destination = "El Bosque",
                    stops = 16,
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


}

