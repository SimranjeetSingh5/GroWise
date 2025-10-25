package com.simranjeet.growise.data.dao

import androidx.room.*
import com.simranjeet.growise.data.model.TransactionEntity
import com.simranjeet.growise.data.model.User
import com.simranjeet.growise.data.model.UserWithTransactions
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionsDao {

    // ---------------------------
    // User operations
    // ---------------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM user LIMIT 1")
    fun getUserFlow(): Flow<User?>

    @Query("DELETE FROM user")
    suspend fun clearUser()

    // ---------------------------
    // Transaction operations
    // ---------------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<TransactionEntity>)

    @Query("SELECT * FROM transactions WHERE userId = :userId")
    fun getTransactionsForUserFlow(userId: String): Flow<List<TransactionEntity>>

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE userId = :userId")
    suspend fun clearTransactionsForUser(userId: String)

    // ---------------------------
    // Relationship queries
    // ---------------------------

    @Transaction
    @Query("SELECT * FROM user")
    fun getUserWithTransactionsFlow(): Flow<List<UserWithTransactions>>
}
