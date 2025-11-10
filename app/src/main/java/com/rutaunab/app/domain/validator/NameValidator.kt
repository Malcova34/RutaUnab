package com.rutaunab.app.domain.validator

object NameValidator {
    
    private const val MIN_LENGTH = 3
    private const val MAX_LENGTH = 50
    
    /**
     * Valida el nombre completo
     */
    fun validate(name: String): ValidationResult {
        val trimmedName = name.trim()

        if (trimmedName.isBlank()) {
            return ValidationResult.Invalid("El nombre no puede estar vacío")
        }

        val length = trimmedName.length
        if (length < MIN_LENGTH) {
            return ValidationResult.Invalid("El nombre debe tener al menos $MIN_LENGTH caracteres")
        }
        if (length > MAX_LENGTH) {
            return ValidationResult.Invalid("El nombre no puede tener más de $MAX_LENGTH caracteres")
        }

        var hasSpace = false
        var hasDigit = false

        for (char in trimmedName) {
            if (char == ' ' && !hasSpace) hasSpace = true
            if (char.isDigit() && !hasDigit) hasDigit = true
            if (hasSpace && hasDigit) break
        }

        return when {
            !hasSpace -> ValidationResult.Invalid("Por favor ingrese su nombre completo")
            hasDigit -> ValidationResult.Invalid("El nombre no puede contener números")
            else -> ValidationResult.Valid
        }
    }
}

