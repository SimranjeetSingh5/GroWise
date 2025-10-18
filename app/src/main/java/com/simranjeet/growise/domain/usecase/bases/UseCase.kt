package com.simranjeet.growise.domain.usecase.bases

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

abstract class UseCase<in Param, out T> {

    private val _resultFlow = MutableSharedFlow<Result<T>>(replay = 1)
    val resultFlow = _resultFlow.asSharedFlow()

    suspend fun execute(param: Param) {
        _resultFlow.emit(Result.Loading)
        try {
            run(param).also { _resultFlow.emit(Result.Success(it)) }
        } catch (e: Exception) {
            _resultFlow.emit(Result.Error(e.localizedMessage ?: "Unknown error"))
        }
    }

    protected abstract suspend fun run(param: Param): T

}