package com.simranjeet.growise.domain.usecase.auth

import com.simranjeet.growise.data.repository.AuthRepository
import com.simranjeet.growise.domain.usecase.bases.UseCase


class SignInUseCase(private val repository: AuthRepository) :
    UseCase<Pair<String, String>, Unit>() {

    override suspend fun run(param: Pair<String, String>) {
        val (email, password) = param
        return repository.signInWithEmail(email, password)
    }
}
