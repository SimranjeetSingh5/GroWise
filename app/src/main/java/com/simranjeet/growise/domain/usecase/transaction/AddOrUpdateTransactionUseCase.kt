package com.simranjeet.growise.domain.usecase.transaction

import com.simranjeet.growise.data.model.TransactionEntity
import com.simranjeet.growise.data.repository.TransactionRepository
import com.simranjeet.growise.domain.usecase.bases.UseCase

class AddOrUpdateTransactionUseCase(private val transactionRepository: TransactionRepository) :
    UseCase<TransactionEntity, Unit>() {
    override suspend fun run(param: TransactionEntity) {
        transactionRepository.addOrUpdateTransaction(param)
    }
}