package com.rutaunab.app.presentation.screens.main.qr

import com.rutaunab.app.domain.model.User

data class QRUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val qrCode: String = "",
    val errorMessage: String? = null
)

