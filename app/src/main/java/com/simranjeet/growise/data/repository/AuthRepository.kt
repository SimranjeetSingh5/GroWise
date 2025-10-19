package com.simranjeet.growise.data.repository

import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.simranjeet.growise.BuildConfig
import com.simranjeet.growise.data.client.SupabaseClient
import com.simranjeet.growise.data.model.AuthResponse
import com.simranjeet.growise.di.DIContainer
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.providers.builtin.IDToken
import java.security.MessageDigest
import java.util.UUID

class AuthRepository(private val supabaseClient: SupabaseClient) {

    suspend fun signUpWithEmail(email: String, password: String) {
         try {
            supabaseClient.client.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            AuthResponse.Success
        } catch (e: Exception) {
            throw IllegalArgumentException(e.localizedMessage)
        }
    }

    suspend fun signInWithEmail(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            throw IllegalArgumentException("Email and password cannot be empty.")
        }

        try {
            supabaseClient.client.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }

            val currentUser = supabaseClient.client.auth.currentUserOrNull()
                ?: throw IllegalStateException("Authentication failed.")

            if (currentUser.confirmedAt == null) {
                supabaseClient.client.auth.signOut()
                throw IllegalStateException("Please confirm your email before logging in.")
            }

        } catch (e: RestException) {
            Log.e("AuthRepository", "Sign in failed", e)
            throw IllegalArgumentException("Invalid email or password.")
        } catch (e: Exception) {
            Log.e("AuthRepository", "Unexpected error", e)
            throw e
        }
    }
    fun createNonce(): String {
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)

        return digest.fold("") { str, it ->
            str + "%02x".format(it)
        }
    }

    suspend fun loginGoogleUser(): Unit {
        val hashedNonce = createNonce()

        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(BuildConfig.SUPABASE_CLIENT_ID)
            .setNonce(hashedNonce)
            .setAutoSelectEnabled(false)
            .setFilterByAuthorizedAccounts(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val credentialManager = CredentialManager.create(DIContainer.appContext)

        try {
            val result = credentialManager.getCredential(
                context = DIContainer.appContext,
                request = request
            )

            val googleIdTokenCredential = GoogleIdTokenCredential
                .createFrom(result.credential.data)

            val googleIdToken = googleIdTokenCredential.idToken

            SupabaseClient.client.auth.signInWith(IDToken) {
                idToken = googleIdToken
                provider = Google
            }


        } catch (e: Exception) {
            Log.e("google", e.localizedMessage ?: "")
            throw IllegalArgumentException(e.localizedMessage)
        }
    }
}