package com.simranjeet.growise.presentation.viewmodels.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simranjeet.growise.data.model.TransactionEntity
import com.simranjeet.growise.domain.usecase.bases.Result
import com.simranjeet.growise.domain.usecase.transaction.AddOrUpdateTransactionUseCase
import com.simranjeet.growise.domain.usecase.transaction.FetchAllTransactionsUseCase
import com.simranjeet.growise.domain.usecase.transaction.FetchTransactionByIdUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TransactionViewModel(
    private val addTransactionUseCase: AddOrUpdateTransactionUseCase,
    private val fetchAllTransactionsUseCase: FetchAllTransactionsUseCase,
    private val fetchTransactionByIdUseCase: FetchTransactionByIdUseCase
) : ViewModel() {
    private val transactionState = MutableSharedFlow<TransactionState>()
    fun getTransactionState(): SharedFlow<TransactionState> = transactionState
    private val _editTransaction = MutableStateFlow<TransactionEntity?>(null)
    val editTransaction: StateFlow<TransactionEntity?> = _editTransaction.asStateFlow()

    init {
        viewModelScope.launch {
            fetchAllTransactionsUseCase.resultFlow.collect { result ->
                onFetchAllTransactionsResult(result)
            }
        }
    }

    fun fetchAllExpenses() = viewModelScope.launch(Dispatchers.IO) {
        fetchAllTransactionsUseCase.execute()
    }

    fun fetchTransactionById(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            fetchTransactionByIdUseCase.execute(id)
        }
        viewModelScope.launch {
            fetchTransactionByIdUseCase.resultFlow.collect { result ->
                when (result) {
                    is Result.Success -> {
                        _editTransaction.value = result.data
                        transactionState.emit(TransactionState.TransactionLoaded(result.data))
                    }

                    is Result.Error -> {
                        transactionState.emit(TransactionState.ShowError(result.message))
                    }

                    Result.Loading -> {
                        transactionState.emit(TransactionState.Loading)
                    }
                }
            }
        }
    }


    fun clearEditTransaction() {
        _editTransaction.value = null
    }

    fun addOrUpdateTransaction(transaction: TransactionEntity) {
        viewModelScope.launch(Dispatchers.IO) { addTransactionUseCase.execute(transaction) }
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
            is Result.Success<List<TransactionEntity>> -> {
                val transactions = state.data
                if (transactions.isEmpty()) {
                    transactionState.emit(TransactionState.Empty)
                } else {
                    transactionState.emit(TransactionState.LoadedTransaction(transactions))
                }
            }
        }
    }

    sealed class TransactionState {
        data class ShowError(val message: String) : TransactionState()
        data object Loading : TransactionState()
        data object Added : TransactionState()
        data object Empty : TransactionState()
        data class LoadedTransaction(val transaction: List<TransactionEntity>) : TransactionState()
        data class TransactionLoaded(val transaction: TransactionEntity?) : TransactionState()

    }

}