package com.rutaunab.app.data.repository

import com.rutaunab.app.data.firebase.firestore.FirestoreDataSource
import com.rutaunab.app.data.firebase.firestore.mapper.UserMapper
import com.rutaunab.app.domain.model.User
import com.rutaunab.app.domain.repository.UserRepository
import com.rutaunab.app.domain.util.Result

class UserRepositoryImpl(
    private val firestore: FirestoreDataSource
) : UserRepository {

    override suspend fun getUserById(userId: String): Result<User> {
        return try {
            val userDTO = firestore.getUser(userId)
                ?: throw Exception("Usuario no encontrado")
            
            Result.Success(UserMapper.toDomain(userDTO))
        } catch (e: Exception) {
            Result.Error(e, "Error al obtener usuario: ${e.message}")
        }
    }

    override suspend fun updateUser(user: User): Result<User> {
        return try {
            val userDTO = UserMapper.toDTO(user)
            firestore.updateUser(user.id, userDTO)
            
            Result.Success(user)
        } catch (e: Exception) {
            Result.Error(e, "Error al actualizar usuario: ${e.message}")
        }
    }

    override suspend fun updateProfileImage(userId: String, imageUri: String): Result<String> {
        return try {
            // TODO: Implementar upload a Firebase Storage
            // Por ahora retornamos la URL directamente
            val userDTO = firestore.getUser(userId)
                ?: throw Exception("Usuario no encontrado")
            
            val updatedDTO = userDTO.copy(profileImageUrl = imageUri)
            firestore.updateUser(userId, updatedDTO)
            
            Result.Success(imageUri)
        } catch (e: Exception) {
            Result.Error(e, "Error al actualizar imagen de perfil: ${e.message}")
        }
    }

    override suspend fun deleteUser(userId: String): Result<Unit> {
        return try {
            firestore.deleteUser(userId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, "Error al eliminar usuario: ${e.message}")
        }
    }
}

