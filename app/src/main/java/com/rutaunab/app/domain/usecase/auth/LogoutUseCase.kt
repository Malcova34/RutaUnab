package com.rutaunab.app.domain.usecase.auth

import com.rutaunab.app.domain.repository.AuthRepository
import com.rutaunab.app.domain.util.Result

class LogoutUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return authRepository.logout()
    }
}

