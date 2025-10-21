package com.simranjeet.growise.presentation.ui.composables

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.simranjeet.growise.GrowiseApp
import com.simranjeet.growise.di.DIContainer
import com.simranjeet.growise.presentation.ui.activities.MainActivity

@Composable
fun AppRouter() {
    val isLoggedIn by GrowiseApp.instance.isLoggedIn.collectAsState()

    if (isLoggedIn) {
        val context = DIContainer.appContext
        context.startActivity(
            Intent(context, MainActivity::class.java).apply {
                flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
    } else {
        AuthNavigator() // User is not logged in
    }
}