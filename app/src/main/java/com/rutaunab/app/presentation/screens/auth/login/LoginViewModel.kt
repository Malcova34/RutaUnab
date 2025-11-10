package com.rutaunab.app.presentation.screens.auth.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rutaunab.app.data.firebase.auth.FirebaseAuthDataSource
import com.rutaunab.app.data.firebase.firestore.FirestoreDataSource
import com.rutaunab.app.data.local.SessionManager
import com.rutaunab.app.data.repository.AuthRepositoryImpl
import com.rutaunab.app.domain.model.UserType
import com.rutaunab.app.domain.usecase.auth.LoginUseCase
import com.rutaunab.app.domain.usecase.auth.GetCurrentUserUseCase
import com.rutaunab.app.domain.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val context: Context? = null,
    private val loginUseCase: LoginUseCase = LoginUseCase(
        AuthRepositoryImpl(
            FirebaseAuthDataSource(),
            FirestoreDataSource()
        )
    ),
    private val getCurrentUserUseCase: GetCurrentUserUseCase = GetCurrentUserUseCase(
        AuthRepositoryImpl(
            FirebaseAuthDataSource(),
            FirestoreDataSource()
        )
    )
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    
    private val sessionManager: SessionManager? by lazy {
        context?.let { SessionManager.getInstance(it) }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(
            email = email,
            isEmailError = false,
            emailErrorMessage = null
        ) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(
            password = password,
            isPasswordError = false,
            passwordErrorMessage = null
        ) }
    }

    fun onLoginClick() {
        if (!validateInputs()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            val result = loginUseCase(
                email = _uiState.value.email,
                password = _uiState.value.password
            )
            
            when (result) {
                is Result.Success -> {
                    val user = result.data
                    val userType = user?.userType ?: UserType.STUDENT
                    
                    // Guardar sesión por 15 días
                    sessionManager?.saveSession(
                        userId = user?.id ?: "",
                        userType = userType.name
                    )
                    
                    _uiState.update { it.copy(
                        isLoading = false,
                        isLoginSuccessful = true,
                        userType = userType,
                        errorMessage = null
                    ) }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        errorMessage = result.message ?: "Error al iniciar sesión"
                    ) }
                }
                is Result.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun validateInputs(): Boolean {
        val email = _uiState.value.email
        val password = _uiState.value.password
        var isValid = true

        // Validar email
        if (email.isBlank()) {
            _uiState.update { it.copy(
                isEmailError = true,
                emailErrorMessage = "El correo es obligatorio"
            ) }
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.update { it.copy(
                isEmailError = true,
                emailErrorMessage = "Correo electrónico inválido"
            ) }
            isValid = false
        }

        // Validar password
        if (password.isBlank()) {
            _uiState.update { it.copy(
                isPasswordError = true,
                passwordErrorMessage = "La contraseña es obligatoria"
            ) }
            isValid = false
        } else if (password.length < 6) {
            _uiState.update { it.copy(
                isPasswordError = true,
                passwordErrorMessage = "La contraseña debe tener al menos 6 caracteres"
            ) }
            isValid = false
        }

        return isValid
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun resetLoginSuccess() {
        _uiState.update { it.copy(isLoginSuccessful = false) }
    }
}

