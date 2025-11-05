package com.rutaunab.app.domain.repository

import com.rutaunab.app.domain.model.User
import com.rutaunab.app.domain.util.Result

interface UserRepository {
    
    /**
     * Obtener un usuario por ID
     */
    suspend fun getUserById(userId: String): Result<User>
    
    /**
     * Actualizar perfil de usuario
     */
    suspend fun updateUser(user: User): Result<User>
    
    /**
     * Actualizar foto de perfil
     */
    suspend fun updateProfileImage(userId: String, imageUri: String): Result<String>
    
    /**
     * Eliminar cuenta de usuario
     */
    suspend fun deleteUser(userId: String): Result<Unit>
}

