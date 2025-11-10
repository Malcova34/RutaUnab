package com.rutaunab.app.domain.validator

object StudentIdValidator {
    
    /**
     * Valida el ID de estudiante UNAB
     * Formato esperado: 9 dígitos (ej: 202012345)
     */
    fun validate(studentId: String): ValidationResult {
        if (studentId.isBlank()) {
            return ValidationResult.Invalid("El ID UNAB no puede estar vacío")
        }

        val length = studentId.length
        if (length != 9) {
            return ValidationResult.Invalid("El ID UNAB debe tener 9 dígitos")
        }

        if (studentId[0] != '2' || studentId[1] != '0') {
            return ValidationResult.Invalid("El ID UNAB debe comenzar con 20")
        }

        for (char in studentId) {
            if (!char.isDigit()) {
                return ValidationResult.Invalid("El ID UNAB solo debe contener números")
            }
        }

        return ValidationResult.Valid
    }
}

