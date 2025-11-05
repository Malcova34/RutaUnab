package com.rutaunab.app.domain.usecase.auth

import com.rutaunab.app.domain.model.User
import com.rutaunab.app.domain.repository.AuthRepository
import com.rutaunab.app.domain.util.Result
import com.rutaunab.app.domain.validator.EmailValidator
import com.rutaunab.app.domain.validator.PasswordValidator

class LoginUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        // Validar email
        val emailValidation = EmailValidator.validateGeneral(email)
        if (!emailValidation.isValid) {
            return Result.Error(
                Exception(emailValidation.errorMessageOrNull ?: "Email inválido")
            )
        }
        
        // Validar contraseña
        if (password.isBlank()) {
            return Result.Error(Exception("La contraseña no puede estar vacía"))
        }
        
        // Intentar login
        return authRepository.login(email, password)
    }
}

