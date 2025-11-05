package com.rutaunab.app.presentation.screens.auth.recovery

data class RecoveryUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val emailSent: Boolean = false,
    val errorMessage: String? = null,
    val isEmailError: Boolean = false
)

