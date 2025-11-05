package com.rutaunab.app.domain.usecase.auth

import com.rutaunab.app.domain.model.User
import com.rutaunab.app.domain.repository.AuthRepository
import com.rutaunab.app.domain.util.Result
import com.rutaunab.app.domain.validator.EmailValidator
import com.rutaunab.app.domain.validator.NameValidator
import com.rutaunab.app.domain.validator.PasswordValidator
import com.rutaunab.app.domain.validator.StudentIdValidator

class RegisterUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        name: String,
        email: String,
        password: String,
        passwordConfirmation: String,
        studentId: String,
        career: String
    ): Result<User> {
        
        // Validar nombre
        val nameValidation = NameValidator.validate(name)
        if (!nameValidation.isValid) {
            return Result.Error(
                Exception(nameValidation.errorMessageOrNull ?: "Nombre inválido")
            )
        }
        
        // Validar email
        val emailValidation = EmailValidator.validate(email)
        if (!emailValidation.isValid) {
            return Result.Error(
                Exception(emailValidation.errorMessageOrNull ?: "Email inválido")
            )
        }
        
        // Validar ID de estudiante
        val studentIdValidation = StudentIdValidator.validate(studentId)
        if (!studentIdValidation.isValid) {
            return Result.Error(
                Exception(studentIdValidation.errorMessageOrNull ?: "ID UNAB inválido")
            )
        }
        
        // Validar carrera
        if (career.isBlank()) {
            return Result.Error(Exception("La carrera es obligatoria"))
        }
        
        // Validar contraseña
        val passwordValidation = PasswordValidator.validate(password)
        if (!passwordValidation.isValid) {
            return Result.Error(
                Exception(passwordValidation.errorMessageOrNull ?: "Contraseña inválida")
            )
        }
        
        // Validar que las contraseñas coincidan
        val matchValidation = PasswordValidator.validateMatch(password, passwordConfirmation)
        if (!matchValidation.isValid) {
            return Result.Error(
                Exception(matchValidation.errorMessageOrNull ?: "Las contraseñas no coinciden")
            )
        }
        
        // Registrar usuario
        return authRepository.register(
            name = name.trim(),
            email = email.trim().lowercase(),
            password = password,
            studentId = studentId.trim(),
            career = career.trim()
        )
    }
}

