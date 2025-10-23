package com.simranjeet.growise.data.repository

import android.util.Log
import com.simranjeet.growise.data.client.SupabaseClient
import com.simranjeet.growise.data.dao.TransactionsDao
import com.simranjeet.growise.data.model.User
import com.simranjeet.growise.data.model.UserType
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.flow.Flow

class UserRepository(
    private val supabaseClient: SupabaseClient,
    private val transactionsDao: TransactionsDao
) {

    suspend fun syncUserFromSupabase(userType: UserType = UserType.EMAIL_PASSWORD) {
        Log.d("SyncDebug", "Checking for session...")
        val session = supabaseClient.client.auth.currentSessionOrNull()
        if (session == null) {
            Log.e("SyncDebug", "Session is NULL. User is not logged in. Exiting sync.")
            return
        }
        val user = session.user
        Log.d("SyncDebug", "Session found for user: ${user?.email}")

        val email = user?.email ?: return
        val localUser = User(
            email = email,
            name = "name",
            loggedInVia = userType
        )

        transactionsDao.insertUser(localUser)
    }

    fun getLocalUserFlow(): Flow<User?> = transactionsDao.getUserFlow()

}