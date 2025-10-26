package com.simranjeet.growise.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(
    tableName = "user"
)
data class User(
    @PrimaryKey
    val email: String,
    val name: String,
    val loggedInVia: UserType = UserType.EMAIL_PASSWORD
)

enum class UserType {
    EMAIL_PASSWORD, GOOGLE
    //FACEBOOK, PHONE user when ready in future.
}
