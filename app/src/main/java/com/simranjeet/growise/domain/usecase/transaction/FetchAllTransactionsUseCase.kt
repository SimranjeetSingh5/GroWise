package com.simranjeet.growise.domain.usecase.transaction

import com.simranjeet.growise.data.model.TransactionEntity
import com.simranjeet.growise.data.repository.TransactionRepository
import com.simranjeet.growise.domain.usecase.bases.UseCaseWithoutParams
import kotlinx.coroutines.flow.Flow

class FetchAllTransactionsUseCase(private val transactionRepository: TransactionRepository) :
    UseCaseWithoutParams<List<TransactionEntity>>() {
    override suspend fun run(): Flow<List<TransactionEntity>> {
        return transactionRepository.fetchAllTransactions()
    }


}