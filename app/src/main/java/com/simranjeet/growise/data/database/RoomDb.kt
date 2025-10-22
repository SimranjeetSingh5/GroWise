package com.simranjeet.growise.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.simranjeet.growise.data.dao.TransactionsDao
import com.simranjeet.growise.data.model.TransactionEntity
import com.simranjeet.growise.data.model.User
@Database(
    entities = [User::class, TransactionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionsDao(): TransactionsDao

}

