package com.simranjeet.growise.di

import android.content.Context
import com.simranjeet.growise.data.client.SupabaseClient
import com.simranjeet.growise.data.repository.AuthRepository
import com.simranjeet.growise.domain.usecase.auth.GoogleSignInUseCase
import com.simranjeet.growise.domain.usecase.auth.SignInUseCase
import com.simranjeet.growise.domain.usecase.auth.SignUpUseCase
import com.simranjeet.growise.presentation.viewmodelfactory.auth.AuthViewModelFactory
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider
import org.kodein.di.singleton

object DIContainer {
    lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    val di = DI {

        // --- Data Layer ---
        bind<SupabaseClient>() with singleton { SupabaseClient }

        // --- Domain Layer ---
        bind<AuthRepository>() with singleton { AuthRepository(instance()) }

        bind<SignUpUseCase>() with provider { SignUpUseCase(instance()) }
        bind<SignInUseCase>() with provider { SignInUseCase(instance()) }
        bind<GoogleSignInUseCase>() with provider { GoogleSignInUseCase(instance()) }

        // Bind factory
        bind<AuthViewModelFactory>() with singleton {
            AuthViewModelFactory(
                signUpUseCase = instance(),
                signInUseCase = instance(),
                googleSignInUseCase = instance()
            )
        }
    }
}