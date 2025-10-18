package com.simranjeet.growise.domain.usecase.bases

abstract class CreateUseCase<in Entity, out Output> {
    abstract suspend fun execute(entity: Entity): Output
}