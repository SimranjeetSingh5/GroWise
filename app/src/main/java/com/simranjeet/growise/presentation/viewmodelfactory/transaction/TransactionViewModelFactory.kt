package com.simranjeet.growise.presentation.viewmodelfactory.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.simranjeet.growise.domain.usecase.transaction.AddOrUpdateTransactionUseCase
import com.simranjeet.growise.domain.usecase.transaction.FetchAllTransactionsUseCase
import com.simranjeet.growise.domain.usecase.transaction.FetchTransactionByIdUseCase
import com.simranjeet.growise.presentation.viewmodels.transaction.TransactionViewModel

class TransactionViewModelFactory(
    private val addOrUpdateTransactionUseCase: AddOrUpdateTransactionUseCase,
    private val fetchAllTransactionsUseCase: FetchAllTransactionsUseCase,
    private val fetchTransactionByIdUseCase: FetchTransactionByIdUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            return TransactionViewModel(
                addOrUpdateTransactionUseCase,
                fetchAllTransactionsUseCase,
                fetchTransactionByIdUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }

}