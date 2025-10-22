package com.simranjeet.growise.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class UserWithTransactions(
    @Embedded val user: User,
    @Relation(
        parentColumn = "email",          // use email as stable identifier for relationship
        entityColumn = "userEmail"       // match this in TransactionEntity
    )
    val transactions: List<TransactionEntity>
)