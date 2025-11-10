package com.rutaunab.app.presentation.screens.auth.register

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rutaunab.app.data.firebase.auth.FirebaseAuthDataSource
import com.rutaunab.app.data.firebase.firestore.FirestoreDataSource
import com.rutaunab.app.data.local.SessionManager
import com.rutaunab.app.data.repository.AuthRepositoryImpl
import com.rutaunab.app.domain.usecase.auth.RegisterUseCase
import com.rutaunab.app.domain.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val context: Context? = null,
    private val registerUseCase: RegisterUseCase = RegisterUseCase(
        AuthRepositoryImpl(
            FirebaseAuthDataSource(),
            FirestoreDataSource()
        )
    )
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()
    
    private val sessionManager: SessionManager? by lazy {
        context?.let { SessionManager.getInstance(it) }
    }

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onStudentIdChange(studentId: String) {
        _uiState.update { it.copy(studentId = studentId) }
    }

    fun onCareerChange(career: String) {
        _uiState.update { it.copy(career = career) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun onPasswordConfirmationChange(passwordConfirmation: String) {
        _uiState.update { it.copy(passwordConfirmation = passwordConfirmation) }
    }

    fun onRegisterClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            val result = registerUseCase(
                name = _uiState.value.name,
                email = _uiState.value.email,
                password = _uiState.value.password,
                passwordConfirmation = _uiState.value.passwordConfirmation,
                studentId = _uiState.value.studentId,
                career = _uiState.value.career
            )
            
            when (result) {
                is Result.Success -> {
                    val user = result.data
                    val userType = user?.userType ?: com.rutaunab.app.domain.model.UserType.STUDENT
                    
                    // Guardar sesión por 15 días
                    sessionManager?.saveSession(
                        userId = user?.id ?: "",
                        userType = userType.name
                    )
                    
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            isRegisterSuccessful = true,
                            errorMessage = null
                        ) 
                    }
                }
                is Result.Error -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = result.message ?: "Error al registrar"
                        ) 
                    }
                }
                is Result.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }
    
    fun resetRegisterSuccess() {
        _uiState.update { it.copy(isRegisterSuccessful = false) }
    }
}

