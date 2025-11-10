package com.rutaunab.app.presentation.screens.main.profile

import com.rutaunab.app.domain.model.User

data class EditProfileUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val name: String = "",
    val email: String = "",
    val studentId: String = "",
    val career: String = "",
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null
)

