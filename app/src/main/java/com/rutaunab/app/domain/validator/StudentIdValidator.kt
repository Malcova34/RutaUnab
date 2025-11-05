package com.rutaunab.app.domain.validator

object StudentIdValidator {
    
    /**
     * Valida el ID de estudiante UNAB
     * Formato esperado: 9 dígitos (ej: 202012345)
     */
    fun validate(studentId: String): ValidationResult {
        return when {
            studentId.isBlank() -> {
                ValidationResult.Invalid("El ID UNAB no puede estar vacío")
            }
            !studentId.all { it.isDigit() } -> {
                ValidationResult.Invalid("El ID UNAB solo debe contener números")
            }
            studentId.length != 9 -> {
                ValidationResult.Invalid("El ID UNAB debe tener 9 dígitos")
            }
            !studentId.startsWith("20") -> {
                ValidationResult.Invalid("El ID UNAB debe comenzar con 20")
            }
            else -> ValidationResult.Valid
        }
    }
}

