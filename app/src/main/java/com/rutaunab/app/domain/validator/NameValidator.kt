package com.rutaunab.app.domain.validator

object NameValidator {
    
    private const val MIN_LENGTH = 3
    private const val MAX_LENGTH = 50
    
    /**
     * Valida el nombre completo
     */
    fun validate(name: String): ValidationResult {
        val trimmedName = name.trim()
        
        return when {
            trimmedName.isBlank() -> {
                ValidationResult.Invalid("El nombre no puede estar vacío")
            }
            trimmedName.length < MIN_LENGTH -> {
                ValidationResult.Invalid("El nombre debe tener al menos $MIN_LENGTH caracteres")
            }
            trimmedName.length > MAX_LENGTH -> {
                ValidationResult.Invalid("El nombre no puede tener más de $MAX_LENGTH caracteres")
            }
            !trimmedName.contains(" ") -> {
                ValidationResult.Invalid("Por favor ingrese su nombre completo")
            }
            trimmedName.any { it.isDigit() } -> {
                ValidationResult.Invalid("El nombre no puede contener números")
            }
            else -> ValidationResult.Valid
        }
    }
}

