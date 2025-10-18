package com.simranjeet.growise.domain.usecase.bases

abstract class DeleteUseCase<in Id, out Output> {
    abstract suspend fun execute(id: Id): Output
}