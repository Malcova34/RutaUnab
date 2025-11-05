package com.rutaunab.app.data.repository

import com.rutaunab.app.data.firebase.auth.FirebaseAuthDataSource
import com.rutaunab.app.data.firebase.firestore.FirestoreDataSource
import com.rutaunab.app.data.firebase.firestore.dto.UserDTO
import com.rutaunab.app.data.firebase.firestore.mapper.UserMapper
import com.rutaunab.app.domain.model.User
import com.rutaunab.app.domain.model.UserType
import com.rutaunab.app.domain.repository.AuthRepository
import com.rutaunab.app.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuthDataSource,
    private val firestore: FirestoreDataSource
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            // Autenticar con Firebase Auth
            val firebaseUser = firebaseAuth.signIn(email, password)
            
            // Obtener datos del usuario de Firestore
            val userDTO = firestore.getUser(firebaseUser.uid)
                ?: throw Exception("Usuario no encontrado en la base de datos")
            
            Result.Success(UserMapper.toDomain(userDTO))
        } catch (e: Exception) {
            val errorMessage = when {
                e.message?.contains("password is invalid") == true ||
                e.message?.contains("wrong-password") == true -> 
                    "Contraseña incorrecta"
                e.message?.contains("user not found") == true ||
                e.message?.contains("no user record") == true -> 
                    "Usuario no encontrado"
                e.message?.contains("network") == true -> 
                    "Error de conexión. Verifica tu internet"
                else -> "Error al iniciar sesión: ${e.message}"
            }
            Result.Error(e, errorMessage)
        }
    }

    override suspend fun register(
        name: String,
        email: String,
        password: String,
        studentId: String?,
        career: String?
    ): Result<User> {
        return try {
            // Crear usuario en Firebase Auth
            val firebaseUser = firebaseAuth.signUp(email, password, name)
            
            // Crear documento de usuario en Firestore con estructura solicitada
            val userDTO = UserDTO(
                id = firebaseUser.uid,
                fullName = name,
                email = email,
                idUnab = studentId ?: "",
                carrera = career ?: "",
                role = "usuario normal",
                createdAd = null, // Se establecerá automáticamente con @ServerTimestamp
                profileImageUrl = null,
                driverInfo = null
            )
            
            firestore.createUser(firebaseUser.uid, userDTO)
            
            Result.Success(UserMapper.toDomain(userDTO))
        } catch (e: Exception) {
            // Manejar errores específicos de Firebase
            val errorMessage = when {
                e.message?.contains("email address is already in use") == true -> 
                    "Este correo ya está registrado"
                e.message?.contains("password is invalid") == true || 
                e.message?.contains("weak-password") == true -> 
                    "La contraseña debe tener al menos 6 caracteres"
                e.message?.contains("email address is badly formatted") == true -> 
                    "El formato del correo es inválido"
                e.message?.contains("network") == true -> 
                    "Error de conexión. Verifica tu internet"
                else -> "Error al registrar: ${e.message}"
            }
            Result.Error(e, errorMessage)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            firebaseAuth.signOut()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, "Error al cerrar sesión: ${e.message}")
        }
    }

    override suspend fun recoverPassword(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, "Error al enviar correo de recuperación: ${e.message}")
        }
    }

    override suspend fun getCurrentUser(): Result<User?> {
        return try {
            val firebaseUser = firebaseAuth.getCurrentUser()
            
            if (firebaseUser == null) {
                return Result.Success(null)
            }
            
            val userDTO = firestore.getUser(firebaseUser.uid)
            val user = userDTO?.let { UserMapper.toDomain(it) }
            
            Result.Success(user)
        } catch (e: Exception) {
            Result.Error(e, "Error al obtener usuario actual: ${e.message}")
        }
    }

    override fun observeAuthState(): Flow<User?> {
        return firebaseAuth.observeAuthState().map { firebaseUser ->
            if (firebaseUser == null) {
                null
            } else {
                try {
                    val userDTO = firestore.getUser(firebaseUser.uid)
                    userDTO?.let { UserMapper.toDomain(it) }
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    override suspend fun isUserLoggedIn(): Boolean {
        return firebaseAuth.isUserLoggedIn()
    }
}

