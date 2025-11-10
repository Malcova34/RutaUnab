package com.rutaunab.app.data.local

import android.content.Context
import android.content.SharedPreferences
import java.util.concurrent.TimeUnit

class SessionManager(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME, 
        Context.MODE_PRIVATE
    )
    
    companion object {
        private const val PREFS_NAME = "ruta_unab_session"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_LOGIN_TIMESTAMP = "login_timestamp"
        private const val KEY_USER_TYPE = "user_type"
        private const val SESSION_DURATION_DAYS = 15L
        
        private var instance: SessionManager? = null
        
        fun getInstance(context: Context): SessionManager {
            return instance ?: synchronized(this) {
                instance ?: SessionManager(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }
    
    /**
     * Guarda la sesión del usuario
     */
    fun saveSession(userId: String, userType: String) {
        prefs.edit().apply {
            putString(KEY_USER_ID, userId)
            putLong(KEY_LOGIN_TIMESTAMP, System.currentTimeMillis())
            putString(KEY_USER_TYPE, userType)
            apply()
        }
    }
    
    /**
     * Verifica si hay una sesión válida (no expirada)
     */
    fun isSessionValid(): Boolean {
        val userId = prefs.getString(KEY_USER_ID, null)
        val loginTimestamp = prefs.getLong(KEY_LOGIN_TIMESTAMP, 0L)
        
        if (userId.isNullOrEmpty() || loginTimestamp == 0L) {
            return false
        }
        
        val currentTime = System.currentTimeMillis()
        val sessionDuration = currentTime - loginTimestamp
        val maxDuration = TimeUnit.DAYS.toMillis(SESSION_DURATION_DAYS)
        
        return sessionDuration < maxDuration
    }
    
    /**
     * Obtiene el ID del usuario guardado
     */
    fun getUserId(): String? {
        return if (isSessionValid()) {
            prefs.getString(KEY_USER_ID, null)
        } else {
            null
        }
    }
    
    /**
     * Obtiene el tipo de usuario guardado
     */
    fun getUserType(): String? {
        return if (isSessionValid()) {
            prefs.getString(KEY_USER_TYPE, null)
        } else {
            null
        }
    }
    
    /**
     * Limpia la sesión
     */
    fun clearSession() {
        prefs.edit().clear().apply()
    }
    
    /**
     * Obtiene los días restantes de la sesión
     */
    fun getDaysRemaining(): Int {
        if (!isSessionValid()) return 0
        
        val loginTimestamp = prefs.getLong(KEY_LOGIN_TIMESTAMP, 0L)
        val currentTime = System.currentTimeMillis()
        val sessionDuration = currentTime - loginTimestamp
        val maxDuration = TimeUnit.DAYS.toMillis(SESSION_DURATION_DAYS)
        val remaining = maxDuration - sessionDuration
        
        return TimeUnit.MILLISECONDS.toDays(remaining).toInt()
    }
}

