package com.simranjeet.growise

import android.app.Application
import android.util.Log
import com.simranjeet.growise.data.client.SupabaseClient
import com.simranjeet.growise.data.model.User
import com.simranjeet.growise.data.repository.UserRepository
import com.simranjeet.growise.di.DIContainer
import io.github.jan.supabase.gotrue.SessionStatus
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.kodein.di.direct
import org.kodein.di.instance

class GrowiseApp : Application() {
    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val supabaseClient: SupabaseClient by lazy { DIContainer.di.direct.instance() }
    private val userRepo: UserRepository by lazy { DIContainer.di.direct.instance() }

    val isLoggedIn: StateFlow<Boolean> by lazy {
        val initialValue = supabaseClient.client.auth.currentSessionOrNull() != null

        supabaseClient.client.auth.sessionStatus
            .map { it is SessionStatus.Authenticated }
            .stateIn(
                scope = applicationScope,
                started = SharingStarted.Eagerly,
                initialValue = initialValue
            )
    }

    val localUser: StateFlow<User?> by lazy {
        userRepo.getLocalUserFlow()
            .stateIn(
                scope = applicationScope,
                started = SharingStarted.Lazily,  // Changed to Lazily to avoid premature query
                initialValue = null
            )
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        DIContainer.init(this)

        // Sync user BEFORE anything else accesses localUser
        applicationScope.launch {
            val currentSession = supabaseClient.client.auth.currentSessionOrNull()
            if (currentSession != null) {
                try {
                    Log.d("GrowiseApp", "Syncing user on app start...")
                    userRepo.syncUserFromSupabase()
                    Log.d("GrowiseApp", "User synced successfully")
                } catch (e: Exception) {
                    Log.e("GrowiseApp", "Failed to sync user: ${e.message}", e)
                }
            }
        }

        // Then monitor for login state changes
        applicationScope.launch {
            isLoggedIn.collect { isAuthenticated ->
                if (isAuthenticated) {
                    try {
                        userRepo.syncUserFromSupabase()
                        Log.d("GrowiseApp", "User synced after login state change")
                    } catch (e: Exception) {
                        Log.e("GrowiseApp", "Failed to sync user: ${e.message}", e)
                    }
                }
            }
        }
    }

    fun signOut() {
        applicationScope.launch(Dispatchers.IO) {
            supabaseClient.client.auth.signOut()
            userRepo.deleteCurrentUser()
        }

    }

    companion object {
        lateinit var instance: GrowiseApp
            private set
    }
}
