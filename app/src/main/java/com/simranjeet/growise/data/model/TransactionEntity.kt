package com.simranjeet.growise.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
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
    ]
)
data class TransactionEntity(
    @PrimaryKey val id: String,
    val userEmail: String,               // foreign key referencing User.email
    val userId: String,
    val amount: String,
    val currency: String = "INR",
    val category: String,
    val subCategory: String?,
    val note: String?,
    val timestamp: String,
    val synced: Boolean = false
)