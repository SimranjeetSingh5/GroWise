package com.simranjeet.growise.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["email"],
            childColumns = ["userEmail"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userEmail"])]
)
data class TransactionEntity(
    @PrimaryKey val id: String,
    val userEmail: String,
    val amount: String,        // ✅ Numeric amount as string
    val currency: String = "INR",  // New field with default value
    val category: String,
    val subCategory: String?,
    val note: String?,         // ✅ Text notes
    val timestamp: String,
    val synced: Boolean = false
)