package com.simranjeet.growise.data.repository

import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.simranjeet.growise.BuildConfig
import com.simranjeet.growise.data.client.SupabaseClient
import com.simranjeet.growise.data.dao.UserDao
import com.simranjeet.growise.data.model.AuthResponse
import com.simranjeet.growise.data.model.User
import com.simranjeet.growise.data.model.UserType
import com.simranjeet.growise.di.DIContainer
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.providers.builtin.IDToken
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import java.security.MessageDigest
import java.util.UUID

class AuthRepository(private val supabaseClient: SupabaseClient, private val userDao: UserDao) {

    suspend fun signUpWithEmail(email: String, password: String) {
        try {
            supabaseClient.client.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            onUserAuthenticated(email, email.substringBefore("@"), UserType.EMAIL_PASSWORD)
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

            // Fix: Extract name properly from JsonElement
            val name = currentUser.userMetadata?.get("full_name")?.jsonPrimitive?.contentOrNull
                ?: currentUser.userMetadata?.get("name")?.jsonPrimitive?.contentOrNull
                ?: email.substringBefore("@")

            onUserAuthenticated(
                email = email,
                name = name,
                loginType = UserType.EMAIL_PASSWORD
            )

        } catch (e: RestException) {
            Log.e("AuthRepository", "Sign in failed", e)
            throw IllegalArgumentException("Invalid email or password.")
        } catch (e: Exception) {
            Log.e("AuthRepository", "Unexpected error", e)
            throw e
        }
    }

    suspend fun loginGoogleUser() {
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

            supabaseClient.client.auth.signInWith(IDToken) {
                idToken = googleIdToken
                provider = Google
            }

            val currentUser = supabaseClient.client.auth.currentUserOrNull()
                ?: throw IllegalStateException("Google authentication failed.")

            val email = currentUser.email
                ?: throw IllegalStateException("Google user has no email")

            val nameFromMetadata =
                currentUser.userMetadata?.get("full_name")?.jsonPrimitive?.contentOrNull
                    ?: currentUser.userMetadata?.get("name")?.jsonPrimitive?.contentOrNull
                    ?: currentUser.userMetadata?.get("display_name")?.jsonPrimitive?.contentOrNull

            // Prioritize Google credential display name, then metadata, then email prefix
            val name = googleIdTokenCredential.displayName
                ?: nameFromMetadata
                ?: email.substringBefore("@")

            Log.d("AuthRepository", "Google user: email=$email, name=$name")

            onUserAuthenticated(
                email = email,
                name = name,
                loginType = UserType.GOOGLE
            )

        } catch (e: Exception) {
            Log.e("google", e.localizedMessage ?: "")
            throw IllegalArgumentException(e.localizedMessage)
        }
    }

    private suspend fun onUserAuthenticated(email: String, name: String, loginType: UserType) {
        val existingUser = userDao.getUserByEmail(email)

        if (existingUser == null) {
            // User doesn't exist, insert new
            val user = User(
                email = email,
                name = name,
                loggedInVia = loginType
            )
            val rowId = userDao.insertUser(user)
            Log.d("AuthRepository", "New user inserted: $email (rowId: $rowId)")
        } else {
            // User exists, only update if needed
            if (existingUser.name != name || existingUser.loggedInVia != loginType) {
                val updatedUser = existingUser.copy(
                    name = name,
                    loggedInVia = loginType
                )
                userDao.updateUser(updatedUser)
                Log.d("AuthRepository", "User updated: $email")
            } else {
                Log.d("AuthRepository", "User already exists, no changes: $email")
            }
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
}