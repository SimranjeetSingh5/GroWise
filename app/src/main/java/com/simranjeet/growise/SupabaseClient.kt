package com.simranjeet.growise

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage


object SupabaseClient {

    val client = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_KEY
    ) {
        install(Auth)
        install(Postgrest)
        install(Realtime)
        install(Storage)
    }

    // Quick test to verify configuration
    fun isConfigured(): Boolean {
        return BuildConfig.SUPABASE_URL.isNotEmpty() &&
                BuildConfig.SUPABASE_URL != "https://localhost" &&
                BuildConfig.SUPABASE_KEY.isNotEmpty()
    }
}