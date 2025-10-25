package com.simranjeet.growise.domain.usecase.auth

import com.simranjeet.growise.data.model.UserType
import com.simranjeet.growise.data.repository.UserRepository
import com.simranjeet.growise.domain.usecase.bases.UseCase

class SyncUserUseCase(private val userRepository: UserRepository) : UseCase<UserType, Unit>() {
    override suspend fun run(param: UserType) {
        userRepository.syncUserFromSupabase(param)
    }
}