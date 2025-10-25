package com.simranjeet.growise

import android.app.Application
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.kodein.di.direct
import org.kodein.di.instance
import kotlin.math.log

class GrowiseApp : Application() {
    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
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

    private val supabaseClient: SupabaseClient by lazy { DIContainer.di.direct.instance() }
    private val userRepo: UserRepository by lazy { DIContainer.di.direct.instance() }
    val isLoggedInFromCache: Boolean by lazy { supabaseClient.client.auth.currentSessionOrNull() != null }


    val localUser: StateFlow<User?> by lazy {
        userRepo.getLocalUserFlow()
            .stateIn(
                scope = applicationScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null
            )
    }

    override fun onCreate() {
        super.onCreate()
        DIContainer.init(this)
        instance = this
        applicationScope.launch {
            isLoggedIn.collect { state ->
                if (state || isLoggedInFromCache) {
                    userRepo.syncUserFromSupabase()
                    Log.d("${this::class.simpleName}", "User synced from Supabase to Room")
                }
            }
        }
    }

    fun signOut() = applicationScope.launch { supabaseClient.client.auth.signOut() }


    companion object {
        lateinit var instance: GrowiseApp
            private set
    }
}
