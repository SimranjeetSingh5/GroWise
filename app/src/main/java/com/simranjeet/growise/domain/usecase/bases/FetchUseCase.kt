package com.simranjeet.growise.domain.usecase.bases

abstract class FetchUseCase<in Id, out Output> {
    abstract suspend fun execute(id: Id): Output
}