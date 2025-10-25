package com.simranjeet.growise.presentation.viewmodels.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simranjeet.growise.data.model.TransactionEntity
import com.simranjeet.growise.domain.usecase.bases.Result
import com.simranjeet.growise.domain.usecase.transaction.AddOrUpdateTransactionUseCase
import com.simranjeet.growise.domain.usecase.transaction.FetchAllTransactionsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class TransactionViewModel(
    private val addTransactionUseCase: AddOrUpdateTransactionUseCase,
    private val fetchAllTransactionsUseCase: FetchAllTransactionsUseCase
) : ViewModel() {

    private var addTransactionJob: Job? = null

    private val transactionState = MutableSharedFlow<TransactionState>()
    fun getTransactionState(): SharedFlow<TransactionState> = transactionState

    init {
        viewModelScope.launch {
            fetchAllTransactionsUseCase.resultFlow.collect { state ->
                onFetchAllTransactionsResult(
                    state
                )
            }
        }
    }

    fun addTransaction(transaction: TransactionEntity) {
        viewModelScope.launch(Dispatchers.IO) { addTransactionUseCase.execute(transaction) }
        addTransactionJob =
            viewModelScope.launch {
                addTransactionUseCase.resultFlow.collect { result ->
                    transactionState.emit(
                        TransactionState.Added
                    )
                }
            }
    }

    private suspend fun onFetchAllTransactionsResult(state: Result<List<TransactionEntity>>) {
        when (state) {
            is Result.Error -> transactionState.emit(TransactionState.ShowError(state.message))
            Result.Loading -> transactionState.emit(TransactionState.Loading)
            is Result.Success<List<TransactionEntity>> -> transactionState.emit(
                TransactionState.LoadedTransaction(
                    state.data
                )
            )
        }
    }

    sealed class TransactionState {
        data class ShowError(val message: String) : TransactionState()
        data object Loading : TransactionState()
        data object Added : TransactionState()
        data class LoadedTransaction(val transaction: List<TransactionEntity>) : TransactionState()
    }

}