package com.simranjeet.growise.domain.usecase.bases

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

abstract class UseCaseWithoutParams<out T> {

    private val _resultFlow = MutableSharedFlow<Result<T>>(replay = 1)
    val resultFlow = _resultFlow.asSharedFlow()

    suspend fun execute() {
        _resultFlow.emit(Result.Loading)
        try {
            run().collect { data ->
                _resultFlow.emit(Result.Success(data))
            }
        } catch (e: Exception) {
            _resultFlow.emit(Result.Error(e.localizedMessage ?: "Unknown error"))
        }
    }

    protected abstract suspend fun run(): Flow<T>
}