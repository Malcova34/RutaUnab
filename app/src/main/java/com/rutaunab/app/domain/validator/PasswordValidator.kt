package com.rutaunab.app.domain.validator

object PasswordValidator {
    
    private const val MIN_LENGTH = 6
    private const val MAX_LENGTH = 50
    
    /**
     * Valida si una contraseña cumple los requisitos
     */
    fun validate(password: String): ValidationResult {
        if (password.isBlank()) {
            return ValidationResult.Invalid("La contraseña no puede estar vacía")
        }

        val length = password.length
        if (length < MIN_LENGTH) {
            return ValidationResult.Invalid("La contraseña debe tener al menos $MIN_LENGTH caracteres")
        }
        if (length > MAX_LENGTH) {
            return ValidationResult.Invalid("La contraseña no puede tener más de $MAX_LENGTH caracteres")
        }

        var hasDigit = false
        var hasLetter = false

        for (char in password) {
            if (char.isDigit() && !hasDigit) hasDigit = true
            if (char.isLetter() && !hasLetter) hasLetter = true
            if (hasDigit && hasLetter) break
        }

        return when {
            !hasDigit -> ValidationResult.Invalid("La contraseña debe contener al menos un número")
            !hasLetter -> ValidationResult.Invalid("La contraseña debe contener al menos una letra")
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
        var hasDigit = false
        var hasLower = false
        var hasUpper = false
        var hasSpecial = false

        if (password.length >= 8) strength++

        for (char in password) {
            when {
                char.isDigit() && !hasDigit -> {
                    hasDigit = true
                    strength++
                }
                char.isLowerCase() && !hasLower -> {
                    hasLower = true
                    strength++
                }
                char.isUpperCase() && !hasUpper -> {
                    hasUpper = true
                    strength++
                }
                !char.isLetterOrDigit() && !hasSpecial -> {
                    hasSpecial = true
                    strength++
                }
            }
            // Early exit if all criteria are met
            if (hasDigit && hasLower && hasUpper && hasSpecial) break
        }

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

