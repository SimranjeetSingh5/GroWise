package com.simranjeet.growise.data.repository

import android.util.Log
import com.simranjeet.growise.GrowiseApp
import com.simranjeet.growise.data.client.SupabaseClient
import com.simranjeet.growise.data.dao.UserDao
import com.simranjeet.growise.data.model.User
import com.simranjeet.growise.data.model.UserType
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

class UserRepository(
    private val supabaseClient: SupabaseClient,
    private val userDao: UserDao
) {

    suspend fun syncUserFromSupabase() {
        val session = supabaseClient.client.auth.currentSessionOrNull()
        if (session == null) {
            Log.w("UserRepository", "No session found, cannot sync user")
            return
        }
        val user = session.user
        val email = user?.email

        // Correct way to extract strings from JsonElement
        val name = user?.userMetadata?.get("full_name")?.jsonPrimitive?.contentOrNull
            ?: user?.userMetadata?.get("name")?.jsonPrimitive?.contentOrNull
            ?: user?.userMetadata?.get("display_name")?.jsonPrimitive?.contentOrNull
            ?: email?.substringBefore("@")
            ?: "Unknown"

        if (email != null) {
            val existingUser = userDao.getUserByEmail(email)

            if (existingUser == null) {
                val user = User(
                    email = email,
                    name = name,
                    loggedInVia = UserType.EMAIL_PASSWORD
                )
                userDao.insertUser(user)
                Log.d("UserRepository", "User synced (new): $email")
            } else {
                Log.d("UserRepository", "User already exists: $email")
                // Only update if needed
                if (existingUser.name != name) {
                    userDao.updateUser(existingUser.copy(name = name))
                    Log.d("UserRepository", "User name updated: $email")
                }
            }
        } else {
            Log.e("UserRepository", "User email is null, cannot sync")
        }
    }

    fun getLocalUserFlow(): Flow<User?> = userDao.getUserFlow()

    suspend fun deleteCurrentUser() =
        GrowiseApp.instance.localUser.value?.let { userDao.deleteUser(it) }

}