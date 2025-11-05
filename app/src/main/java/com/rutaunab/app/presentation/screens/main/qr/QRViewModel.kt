package com.rutaunab.app.presentation.screens.main.qr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rutaunab.app.data.firebase.auth.FirebaseAuthDataSource
import com.rutaunab.app.data.firebase.firestore.FirestoreDataSource
import com.rutaunab.app.data.repository.AuthRepositoryImpl
import com.rutaunab.app.domain.usecase.auth.GetCurrentUserUseCase
import com.rutaunab.app.domain.util.onSuccess
import com.rutaunab.app.domain.util.onError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QRViewModel(
    // TODO: Inyectar con Hilt cuando esté configurado
    private val getCurrentUserUseCase: GetCurrentUserUseCase = GetCurrentUserUseCase(
        AuthRepositoryImpl(
            FirebaseAuthDataSource(),
            FirestoreDataSource()
        )
    )
) : ViewModel() {

    private val _uiState = MutableStateFlow(QRUiState())
    val uiState: StateFlow<QRUiState> = _uiState.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // getCurrentUser() retorna Result<User?>, NO Flow, por eso NO se usa .collect()
            val result = getCurrentUserUseCase()
            
            result.onSuccess { user ->
                // Generar código QR basado en el ID del usuario
                val qrCodeData = user?.id ?: ""
                
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        user = user,
                        qrCode = qrCodeData
                    )
                }
            }.onError { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = exception.message
                    )
                }
            }
        }
    }
}

