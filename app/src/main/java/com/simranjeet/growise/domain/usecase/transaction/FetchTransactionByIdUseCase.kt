package com.simranjeet.growise.domain.usecase.transaction

import com.simranjeet.growise.data.model.TransactionEntity
import com.simranjeet.growise.data.repository.TransactionRepository
import com.simranjeet.growise.domain.usecase.bases.UseCase

class FetchTransactionByIdUseCase(
    private val transactionRepository: TransactionRepository
) : UseCase<String, TransactionEntity?>() {

    override suspend fun run(param: String): TransactionEntity? {
        return transactionRepository.getTransactionById(param)
    }
}