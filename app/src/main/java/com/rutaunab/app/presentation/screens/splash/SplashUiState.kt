package com.rutaunab.app.presentation.screens.splash

data class SplashUiState(
    val isLoading: Boolean = true,
    val isAuthCheckComplete: Boolean = false,
    val isUserLoggedIn: Boolean = false
)

