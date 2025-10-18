package com.simranjeet.growise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.simranjeet.growise.presentation.composables.RegisterScreen
import com.simranjeet.growise.ui.theme.GroWiseTheme

class SignupActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { GroWiseTheme { RegisterScreen() } }
    }

}