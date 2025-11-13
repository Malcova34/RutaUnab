package com.rutaunab.app.presentation.screens.main.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rutaunab.app.data.firebase.auth.FirebaseAuthDataSource
import com.rutaunab.app.data.firebase.firestore.FirestoreDataSource
import com.rutaunab.app.data.repository.AuthRepositoryImpl
import com.rutaunab.app.data.repository.UserRepositoryImpl
import com.rutaunab.app.domain.model.User
import com.rutaunab.app.domain.repository.UserRepository
import com.rutaunab.app.domain.usecase.auth.GetCurrentUserUseCase
import com.rutaunab.app.domain.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditProfileViewModel(

    private val getCurrentUserUseCase: GetCurrentUserUseCase = GetCurrentUserUseCase(
        AuthRepositoryImpl(
            FirebaseAuthDataSource(),
            FirestoreDataSource()
        )
    ),
    private val userRepository: UserRepository = UserRepositoryImpl(
        FirestoreDataSource()
    )
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            when (val result = getCurrentUserUseCase()) {
                is Result.Success -> {
                    val user = result.data
                    if (user != null) {
                        _uiState.update {
                            it.copy(
                                user = user,
                                name = user.name,
                                email = user.email,
                                studentId = user.studentId,
                                career = user.career,
                                isLoading = false,
                                errorMessage = null
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Usuario no encontrado"
                            )
                        }
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

    fun onNameChanged(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onCareerChanged(career: String) {
        _uiState.update { it.copy(career = career) }
    }

    fun saveProfile() {
        viewModelScope.launch {
            val currentUser = _uiState.value.user ?: return@launch
            
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            
            val updatedUser = currentUser.copy(
                name = _uiState.value.name,
                email = _uiState.value.email,
                career = _uiState.value.career
            )
            
            try {
                when (val result = userRepository.updateUser(updatedUser)) {
                    is Result.Success -> {
                        _uiState.update {
                            it.copy(
                                user = updatedUser,
                                isSaving = false,
                                saveSuccess = true,
                                errorMessage = null
                            )
                        }
                    }
                    is Result.Error -> {
                        _uiState.update {
                            it.copy(
                                isSaving = false,
                                saveSuccess = false,
                                errorMessage = result.exception.message ?: "Error al guardar"
                            )
                        }
                    }
                    is Result.Loading -> {
                        // No hacer nada, ya está en isSaving = true
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        saveSuccess = false,
                        errorMessage = e.message ?: "Error desconocido"
                    )
                }
            }
        }
    }

    fun resetSaveSuccess() {
        _uiState.update { it.copy(saveSuccess = false) }
    }
}

