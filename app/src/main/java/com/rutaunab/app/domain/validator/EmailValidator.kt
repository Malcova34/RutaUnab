package com.rutaunab.app.domain.validator

import android.util.Patterns

object EmailValidator {
    
    /**
     * Valida si un email es válido
     */
    fun validate(email: String): ValidationResult {
        return when {
            email.isBlank() -> {
                ValidationResult.Invalid("El correo electrónico no puede estar vacío")
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                ValidationResult.Invalid("El formato del correo electrónico no es válido")
            }
            !email.endsWith("@unab.com") && !email.endsWith("@.com") -> {
                ValidationResult.Invalid("Debe usar su correo institucional UNAB (@unab.cl)")
            }
            else -> ValidationResult.Valid
        }
    }
    
    /**
     * Valida email sin restricción de dominio UNAB (para admin)
     */
    fun validateGeneral(email: String): ValidationResult {
        return when {
            email.isBlank() -> {
                ValidationResult.Invalid("El correo electrónico no puede estar vacío")
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                ValidationResult.Invalid("El formato del correo electrónico no es válido")
            }
            else -> ValidationResult.Valid
        }
    }
}

