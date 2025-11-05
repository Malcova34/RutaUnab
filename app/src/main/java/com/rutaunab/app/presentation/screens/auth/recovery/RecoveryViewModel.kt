package com.rutaunab.app.presentation.screens.auth.recovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rutaunab.app.data.firebase.auth.FirebaseAuthDataSource
import com.rutaunab.app.data.firebase.firestore.FirestoreDataSource
import com.rutaunab.app.data.repository.AuthRepositoryImpl
import com.rutaunab.app.domain.usecase.auth.RecoverPasswordUseCase
import com.rutaunab.app.domain.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecoveryViewModel(
    private val recoverPasswordUseCase: RecoverPasswordUseCase = RecoverPasswordUseCase(
        AuthRepositoryImpl(
            FirebaseAuthDataSource(),
            FirestoreDataSource()
        )
    )
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecoveryUiState())
    val uiState: StateFlow<RecoveryUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onSendRecoveryEmail() {
        val email = _uiState.value.email
        
        // Validar email básico
        if (email.isBlank()) {
            _uiState.update { it.copy(errorMessage = "El correo electrónico es obligatorio") }
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            val result = recoverPasswordUseCase(email)
            
            when (result) {
                is Result.Success -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        emailSent = true,
                        errorMessage = null
                    ) }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        errorMessage = result.message ?: "Error al enviar correo de recuperación"
                    ) }
                }
                is Result.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun resetEmailSent() {
        _uiState.update { it.copy(emailSent = false) }
    }
}

