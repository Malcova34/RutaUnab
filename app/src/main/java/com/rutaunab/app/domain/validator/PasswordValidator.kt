package com.rutaunab.app.domain.validator

object PasswordValidator {
    
    private const val MIN_LENGTH = 6
    private const val MAX_LENGTH = 50
    
    /**
     * Valida si una contraseña cumple los requisitos
     */
    fun validate(password: String): ValidationResult {
        return when {
            password.isBlank() -> {
                ValidationResult.Invalid("La contraseña no puede estar vacía")
            }
            password.length < MIN_LENGTH -> {
                ValidationResult.Invalid("La contraseña debe tener al menos $MIN_LENGTH caracteres")
            }
            password.length > MAX_LENGTH -> {
                ValidationResult.Invalid("La contraseña no puede tener más de $MAX_LENGTH caracteres")
            }
            !password.any { it.isDigit() } -> {
                ValidationResult.Invalid("La contraseña debe contener al menos un número")
            }
            !password.any { it.isLetter() } -> {
                ValidationResult.Invalid("La contraseña debe contener al menos una letra")
            }
            else -> ValidationResult.Valid
        }
    }
    
    /**
     * Valida que dos contraseñas coincidan
     */
    fun validateMatch(password: String, confirmPassword: String): ValidationResult {
        return when {
            password != confirmPassword -> {
                ValidationResult.Invalid("Las contraseñas no coinciden")
            }
            else -> ValidationResult.Valid
        }
    }
    
    /**
     * Obtiene la fuerza de la contraseña (débil, media, fuerte)
     */
    fun getStrength(password: String): PasswordStrength {
        if (password.length < MIN_LENGTH) return PasswordStrength.WEAK
        
        var strength = 0
        
        if (password.length >= 8) strength++
        if (password.any { it.isDigit() }) strength++
        if (password.any { it.isLowerCase() }) strength++
        if (password.any { it.isUpperCase() }) strength++
        if (password.any { !it.isLetterOrDigit() }) strength++
        
        return when {
            strength <= 2 -> PasswordStrength.WEAK
            strength == 3 -> PasswordStrength.MEDIUM
            else -> PasswordStrength.STRONG
        }
    }
    
    enum class PasswordStrength {
        WEAK, MEDIUM, STRONG
    }
}

