package com.simranjeet.growise.presentation.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.simranjeet.growise.presentation.ui.composables.AppRouter
import com.simranjeet.growise.presentation.ui.composables.AuthNavigator
import com.simranjeet.growise.presentation.ui.theme.GroWiseTheme

class SignupActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { GroWiseTheme { AppRouter() } }
    }

}