package com.simranjeet.growise.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "user",
    indices = [Index(value = ["email"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val email: String,
    val name: String,
    val loggedInVia: UserType = UserType.EMAIL_PASSWORD
)

enum class UserType {
    EMAIL_PASSWORD, GOOGLE
    //FACEBOOK, PHONE user when ready in future.
}
