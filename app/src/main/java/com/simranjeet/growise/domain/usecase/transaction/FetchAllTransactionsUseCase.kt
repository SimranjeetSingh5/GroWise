package com.simranjeet.growise.domain.usecase.transaction

import android.util.Log
import com.simranjeet.growise.GrowiseApp
import com.simranjeet.growise.data.model.TransactionEntity
import com.simranjeet.growise.data.repository.TransactionRepository
import com.simranjeet.growise.domain.usecase.bases.FlowUseCaseWithoutParams
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach

class FetchAllTransactionsUseCase(private val transactionRepository: TransactionRepository) :
    FlowUseCaseWithoutParams<List<TransactionEntity>>() {

    override suspend fun run(): Flow<List<TransactionEntity>> {
        return GrowiseApp.instance.localUser
            .onEach { Log.d("LocalUser", "Emitted user: $it") }
            .filterNotNull()
            .flatMapLatest { user ->
                Log.d("FetchTransactions", "Fetching for: ${user.email}")
                transactionRepository.fetchAllTransactions(user.email)
            }
    }
}