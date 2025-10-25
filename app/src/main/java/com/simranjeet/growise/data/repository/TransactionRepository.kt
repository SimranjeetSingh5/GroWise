package com.simranjeet.growise.data.repository

import com.simranjeet.growise.GrowiseApp
import com.simranjeet.growise.data.dao.TransactionsDao
import com.simranjeet.growise.data.model.TransactionEntity
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val transactionsDao: TransactionsDao) {

    suspend fun addOrUpdateTransaction(transaction: TransactionEntity) {
        transactionsDao.insertTransaction(transaction)
    }

    suspend fun fetchAllTransactions(): Flow<List<TransactionEntity>> {
        val email = GrowiseApp.instance.localUser.value?.email
            ?: throw IllegalStateException("No Data found")
        return transactionsDao.getTransactionsForUserFlow(email)
    }
}