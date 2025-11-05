package com.rutaunab.app.domain.usecase.auth

import com.rutaunab.app.domain.repository.AuthRepository
import com.rutaunab.app.domain.util.Result
import com.rutaunab.app.domain.validator.EmailValidator

class RecoverPasswordUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<Unit> {
        // Validar email
        val emailValidation = EmailValidator.validateGeneral(email)
        if (!emailValidation.isValid) {
            return Result.Error(
                Exception(emailValidation.errorMessageOrNull ?: "Email inválido")
            )
        }
        
        // Recuperar contraseña
        return authRepository.recoverPassword(email.trim().lowercase())
    }
}

