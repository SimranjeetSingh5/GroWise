package com.simranjeet.growise.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.simranjeet.growise.data.model.TransactionEntity
import com.simranjeet.growise.data.model.UserWithTransactions
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<TransactionEntity>)


    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    suspend fun getTransactionById(id: String): TransactionEntity?

    @Query("SELECT * FROM transactions WHERE userEmail = :userEmail")
    fun getTransactionsForUserFlow(userEmail: String): Flow<List<TransactionEntity>>

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    // ---------------------------
    // Relationship queries
    // ---------------------------

    @Transaction
    @Query("SELECT * FROM user")
    fun getUserWithTransactionsFlow(): Flow<List<UserWithTransactions>>
}
