package com.simranjeet.growise.domain.usecase.bases

abstract class FetchAllUseCase<out Output> {
    abstract suspend fun execute(): Output
}