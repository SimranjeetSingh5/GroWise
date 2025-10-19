package com.simranjeet.growise.domain.usecase.auth

import com.simranjeet.growise.data.model.AuthResponse
import com.simranjeet.growise.data.repository.AuthRepository
import com.simranjeet.growise.domain.usecase.bases.UseCaseWithoutParams

class GoogleSignInUseCase(
    private val repository: AuthRepository
) : UseCaseWithoutParams<Unit>() {


    override suspend fun run(): Unit {
        return repository.loginGoogleUser()
    }
}