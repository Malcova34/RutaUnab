package com.rutaunab.app.presentation.screens.main.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rutaunab.app.domain.usecase.auth.GetCurrentUserUseCase
import com.rutaunab.app.domain.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

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
}

