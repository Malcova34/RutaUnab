package com.rutaunab.app.domain.validator

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val errorMessage: String) : ValidationResult()
    
    val isValid: Boolean
        get() = this is Valid
    
    val errorMessageOrNull: String?
        get() = (this as? Invalid)?.errorMessage
}

