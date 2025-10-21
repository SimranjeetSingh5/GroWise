package com.simranjeet.growise

import android.app.Application
import com.simranjeet.growise.data.client.SupabaseClient
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
import org.kodein.di.direct
import org.kodein.di.instance

class GrowiseApp : Application() {
     val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    lateinit var isLoggedIn: StateFlow<Boolean>
        private set

    private val supabaseClient: SupabaseClient by lazy { DIContainer.di.direct.instance() }
    val isLoggedInFromCache: Boolean by lazy {  supabaseClient.client.auth.currentSessionOrNull() != null}


    override fun onCreate() {
        super.onCreate()
        DIContainer.init(this)
        instance = this
        isLoggedIn = supabaseClient.client.auth.sessionStatus
            .map { it is SessionStatus.Authenticated }
            .stateIn(
                scope = applicationScope,
                started = SharingStarted.Eagerly,
                initialValue = isLoggedInFromCache
            )
    }
    companion object {
        lateinit var instance: GrowiseApp
            private set
    }
}
