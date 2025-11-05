package com.rutaunab.app.presentation.screens.auth.register

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val studentId: String = "",
    val career: String = "",
    val password: String = "",
    val passwordConfirmation: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRegisterSuccessful: Boolean = false
)

