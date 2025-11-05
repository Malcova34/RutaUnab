package com.rutaunab.app.domain.usecase.auth

import com.rutaunab.app.domain.model.User
import com.rutaunab.app.domain.repository.AuthRepository
import com.rutaunab.app.domain.util.Result

class GetCurrentUserUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<User?> {
        return authRepository.getCurrentUser()
    }
}

