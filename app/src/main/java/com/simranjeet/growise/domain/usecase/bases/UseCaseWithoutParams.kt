package com.simranjeet.growise.domain.usecase.bases

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

abstract class UseCaseWithoutParams<out T> {

    private val _resultFlow = MutableSharedFlow<Result<T>>(replay = 1)
    val resultFlow = _resultFlow.asSharedFlow()

    suspend fun execute() {
        _resultFlow.emit(Result.Loading)
        try {
            run().also { _resultFlow.emit(Result.Success(it)) }
        } catch (e: Exception) {
            _resultFlow.emit(Result.Error(e.localizedMessage ?: "Unknown error"))
        }
    }

    protected abstract suspend fun run(): T
}