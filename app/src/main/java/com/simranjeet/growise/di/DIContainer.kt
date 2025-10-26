package com.simranjeet.growise.di

import android.content.Context
import androidx.room.Room
import com.simranjeet.growise.data.client.SupabaseClient
import com.simranjeet.growise.data.dao.TransactionsDao
import com.simranjeet.growise.data.dao.UserDao
import com.simranjeet.growise.data.database.AppDatabase
import com.simranjeet.growise.data.repository.AuthRepository
import com.simranjeet.growise.data.repository.TransactionRepository
import com.simranjeet.growise.data.repository.UserRepository
import com.simranjeet.growise.domain.usecase.auth.GoogleSignInUseCase
import com.simranjeet.growise.domain.usecase.auth.SignInUseCase
import com.simranjeet.growise.domain.usecase.auth.SignUpUseCase
import com.simranjeet.growise.domain.usecase.transaction.AddOrUpdateTransactionUseCase
import com.simranjeet.growise.domain.usecase.transaction.FetchAllTransactionsUseCase
import com.simranjeet.growise.domain.usecase.transaction.FetchTransactionByIdUseCase
import com.simranjeet.growise.presentation.viewmodelfactory.auth.AuthViewModelFactory
import com.simranjeet.growise.presentation.viewmodelfactory.transaction.TransactionViewModelFactory
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider
import org.kodein.di.singleton

object DIContainer {
    lateinit var appContext: Context
    lateinit var di: DI


    fun init(context: Context) {
        appContext = context.applicationContext

        di = DI {

            // --- Database Layer ---
            bind<AppDatabase>() with singleton {
                Room.databaseBuilder(
                    appContext,
                    AppDatabase::class.java,
                    "growise_db"
                ).build()
            }

            bind<TransactionsDao>() with singleton { instance<AppDatabase>().transactionsDao() }
            bind<UserDao>() with singleton { instance<AppDatabase>().userDao() }


            // --- Data Layer ---
            bind<SupabaseClient>() with singleton { SupabaseClient }

            // --- Domain Layer ---
            bind<AuthRepository>() with singleton { AuthRepository(instance(), instance()) }
            bind<UserRepository>() with singleton { UserRepository(instance(), instance()) }
            bind<TransactionRepository>() with singleton {
                TransactionRepository(
                    instance(),
                    instance()
                )
            }


            //Auth
            bind<SignUpUseCase>() with provider { SignUpUseCase(instance()) }
            bind<SignInUseCase>() with provider { SignInUseCase(instance()) }
            bind<GoogleSignInUseCase>() with provider { GoogleSignInUseCase(instance()) }

            //Transaction
            bind<AddOrUpdateTransactionUseCase>() with provider {
                AddOrUpdateTransactionUseCase(
                    instance()
                )
            }
            bind<FetchAllTransactionsUseCase>() with provider { FetchAllTransactionsUseCase(instance()) }
            bind<FetchTransactionByIdUseCase>() with provider { FetchTransactionByIdUseCase(instance()) }

            // Bind factory
            bind<AuthViewModelFactory>() with singleton {
                AuthViewModelFactory(
                    instance(), instance(), instance()
                )
            }
            bind<TransactionViewModelFactory>() with singleton {
                TransactionViewModelFactory(
                    instance(), instance(), instance(),
                )
            }
        }
    }
}