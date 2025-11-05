package com.rutaunab.app.domain.repository

import com.rutaunab.app.domain.model.User
import com.rutaunab.app.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    
    /**
     * Iniciar sesión con email y contraseña
     */
    suspend fun login(email: String, password: String): Result<User>
    
    /**
     * Registrar un nuevo usuario
     */
    suspend fun register(
        name: String,
        email: String,
        password: String,
        studentId: String?,
        career: String?
    ): Result<User>
    
    /**
     * Cerrar sesión
     */
    suspend fun logout(): Result<Unit>
    
    /**
     * Recuperar contraseña
     */
    suspend fun recoverPassword(email: String): Result<Unit>
    
    /**
     * Obtener el usuario actualmente autenticado
     */
    suspend fun getCurrentUser(): Result<User?>
    
    /**
     * Observar cambios en el estado de autenticación
     */
    fun observeAuthState(): Flow<User?>
    
    /**
     * Verificar si hay un usuario autenticado
     */
    suspend fun isUserLoggedIn(): Boolean
}

