package com.rutaunab.app.data.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesManager(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )
    
    private val _isDarkMode = MutableStateFlow(getDarkMode())
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()
    
    private val _language = MutableStateFlow(getLanguage())
    val language: StateFlow<String> = _language.asStateFlow()
    
    companion object {
        private const val PREFS_NAME = "app_preferences"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_ROUTE_ALERTS = "route_alerts"
        private const val KEY_SCHEDULE_REMINDERS = "schedule_reminders"
        
        private var instance: PreferencesManager? = null
        
        fun getInstance(context: Context): PreferencesManager {
            return instance ?: synchronized(this) {
                instance ?: PreferencesManager(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }
    
    // Dark Mode
    fun setDarkMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
        _isDarkMode.value = enabled
    }
    
    fun getDarkMode(): Boolean {
        return prefs.getBoolean(KEY_DARK_MODE, false)
    }
    
    // Language
    fun setLanguage(language: String) {
        prefs.edit().putString(KEY_LANGUAGE, language).apply()
        _language.value = language
    }
    
    fun getLanguage(): String {
        return prefs.getString(KEY_LANGUAGE, "es") ?: "es" // "es" o "en"
    }
    
    // Notificaciones
    fun setNotificationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply()
    }
    
    fun getNotificationsEnabled(): Boolean {
        return prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
    }
    
    // Alertas de Ruta
    fun setRouteAlertsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_ROUTE_ALERTS, enabled).apply()
    }
    
    fun getRouteAlertsEnabled(): Boolean {
        return prefs.getBoolean(KEY_ROUTE_ALERTS, true)
    }
    
    // Recordatorios de Horario
    fun setScheduleRemindersEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SCHEDULE_REMINDERS, enabled).apply()
    }
    
    fun getScheduleRemindersEnabled(): Boolean {
        return prefs.getBoolean(KEY_SCHEDULE_REMINDERS, false)
    }
}

