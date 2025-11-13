package com.rutaunab.app.presentation.screens.main.settings

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rutaunab.app.data.firebase.auth.FirebaseAuthDataSource
import com.rutaunab.app.data.firebase.firestore.FirestoreDataSource
import com.rutaunab.app.data.local.PreferencesManager
import com.rutaunab.app.data.local.SessionManager
import com.rutaunab.app.data.repository.AuthRepositoryImpl
import com.rutaunab.app.data.worker.WorkManagerHelper
import com.rutaunab.app.domain.usecase.auth.GetCurrentUserUseCase
import com.rutaunab.app.domain.usecase.auth.LogoutUseCase
import com.rutaunab.app.domain.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*

class SettingsViewModel(
    private val context: Context? = null,

    private val getCurrentUserUseCase: GetCurrentUserUseCase = GetCurrentUserUseCase(
        AuthRepositoryImpl(
            FirebaseAuthDataSource(),
            FirestoreDataSource()
        )
    ),
    private val logoutUseCase: LogoutUseCase = LogoutUseCase(
        AuthRepositoryImpl(
            FirebaseAuthDataSource(),
            FirestoreDataSource()
        )
    )
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    private val sessionManager: SessionManager? by lazy {
        context?.let { SessionManager.getInstance(it) }
    }
    
    private val preferencesManager: PreferencesManager? by lazy {
        context?.let { PreferencesManager.getInstance(it) }
    }

    init {
        loadUserData()
        loadPreferences()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            when (val result = getCurrentUserUseCase()) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            user = result.data,
                            isLoading = false,
                            errorMessage = null
                        )
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

    private fun loadPreferences() {
        preferencesManager?.let { prefs ->
            _uiState.update {
                it.copy(
                    pushNotificationsEnabled = prefs.getNotificationsEnabled(),
                    routeAlertsEnabled = prefs.getRouteAlertsEnabled(),
                    scheduleRemindersEnabled = prefs.getScheduleRemindersEnabled(),
                    darkModeEnabled = prefs.getDarkMode(),
                    language = prefs.getLanguage()
                )
            }
        }
    }

    fun togglePushNotifications(enabled: Boolean) {
        preferencesManager?.setNotificationsEnabled(enabled)
        _uiState.update { it.copy(pushNotificationsEnabled = enabled) }
    }

    fun toggleRouteAlerts(enabled: Boolean) {
        preferencesManager?.setRouteAlertsEnabled(enabled)
        _uiState.update { it.copy(routeAlertsEnabled = enabled) }
        
        // Activar/desactivar worker de proximidad
        context?.let { ctx ->
            if (enabled) {
                WorkManagerHelper.scheduleRouteProximityWork(ctx)
            } else {
                WorkManagerHelper.cancelRouteProximityWork(ctx)
            }
        }
    }

    fun toggleScheduleReminders(enabled: Boolean) {
        preferencesManager?.setScheduleRemindersEnabled(enabled)
        _uiState.update { it.copy(scheduleRemindersEnabled = enabled) }
    }

    fun toggleDarkMode(enabled: Boolean) {
        preferencesManager?.setDarkMode(enabled)
        _uiState.update { it.copy(darkModeEnabled = enabled) }
        
        // Reiniciar actividad para aplicar el tema
        (context as? Activity)?.recreate()
    }
    
    fun showLanguageDialog(show: Boolean) {
        _uiState.update { it.copy(showLanguageDialog = show) }
    }
    
    fun changeLanguage(languageCode: String) {
        preferencesManager?.setLanguage(languageCode)
        _uiState.update { it.copy(language = languageCode, showLanguageDialog = false) }
        
        // Cambiar idioma de la app
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        
        context?.let { ctx ->
            val config = ctx.resources.configuration
            config.setLocale(locale)
            ctx.createConfigurationContext(config)
            
            // Reiniciar actividad para aplicar cambios
            (ctx as? Activity)?.recreate()
        }
    }

    fun logout(onLogoutComplete: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoggingOut = true) }
            
            when (val result = logoutUseCase()) {
                is Result.Success -> {
                    // Limpiar sesión guardada
                    sessionManager?.clearSession()
                    
                    _uiState.update {
                        it.copy(
                            isLoggingOut = false,
                            errorMessage = null
                        )
                    }
                    onLogoutComplete()
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoggingOut = false,
                            errorMessage = result.exception.message ?: "Error al cerrar sesión"
                        )
                    }
                }
                is Result.Loading -> {
                    _uiState.update { it.copy(isLoggingOut = true) }
                }
            }
        }
    }
}

