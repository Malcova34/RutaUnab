package com.rutaunab.app.presentation.screens.auth.login

import com.rutaunab.app.domain.model.UserType

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isEmailError: Boolean = false,
    val isPasswordError: Boolean = false,
    val emailErrorMessage: String? = null,
    val passwordErrorMessage: String? = null,
    val isLoginSuccessful: Boolean = false,
    val userType: UserType? = null
)

