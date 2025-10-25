package com.simranjeet.growise.domain.usecase.auth

import com.simranjeet.growise.data.repository.AuthRepository
import com.simranjeet.growise.domain.usecase.bases.UseCaseWithoutParams
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class GoogleSignInUseCase(
    private val repository: AuthRepository
) : UseCaseWithoutParams<Unit>() {


    override suspend fun run(): Flow<Unit> {
        repository.loginGoogleUser()
        return flowOf(Unit)
    }
}