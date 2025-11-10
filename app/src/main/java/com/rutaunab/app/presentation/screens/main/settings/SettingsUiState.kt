package com.rutaunab.app.presentation.screens.main.settings

import com.rutaunab.app.domain.model.User

data class SettingsUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val pushNotificationsEnabled: Boolean = true,
    val routeAlertsEnabled: Boolean = true,
    val scheduleRemindersEnabled: Boolean = false,
    val darkModeEnabled: Boolean = false,
    val language: String = "es", // "es" o "en"
    val isLoggingOut: Boolean = false,
    val errorMessage: String? = null,
    val showLanguageDialog: Boolean = false
)

