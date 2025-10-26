package com.simranjeet.growise.data.repository

import android.util.Log
import com.simranjeet.growise.data.dao.TransactionsDao
import com.simranjeet.growise.data.dao.UserDao
import com.simranjeet.growise.data.model.TransactionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

class TransactionRepository(
    private val userDao: UserDao,
    private val transactionsDao: TransactionsDao
) {

    suspend fun addOrUpdateTransaction(transaction: TransactionEntity): Unit {
        // Verify user exists
        val userExists = userDao.getUserByEmail(transaction.userEmail) != null
        if (!userExists) {
            // Handle error - user must exist
            throw IllegalStateException("User must exist before adding transactions")
        }
        transactionsDao.insertTransaction(transaction)
    }

    suspend fun getTransactionById(id: String): TransactionEntity? {
        return transactionsDao.getTransactionById(id)
    }

    suspend fun fetchAllTransactions(email: String): Flow<List<TransactionEntity>> {
        return transactionsDao.getTransactionsForUserFlow(email)
            .onStart { Log.d("## Repo", "Fetching transactions for $email") }
            .onEach { Log.d("## Repo", "Emitted ${it.size} items") }
    }
}