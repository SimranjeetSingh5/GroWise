package com.simranjeet.growise.domain.usecase.auth


import com.simranjeet.growise.data.model.AuthResponse
import com.simranjeet.growise.data.repository.AuthRepository
import com.simranjeet.growise.domain.usecase.bases.UseCase

class SignUpUseCase(
    private val repository: AuthRepository
) : UseCase<Pair<String, String>, AuthResponse>() {

    override suspend fun run(param: Pair<String, String>): AuthResponse {
        val (email, password) = param
        return repository.signUpWithEmail(email, password)
    }
}