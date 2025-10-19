package com.simranjeet.growise.presentation.ui.composables

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.simranjeet.growise.di.DIContainer
import com.simranjeet.growise.domain.usecase.bases.Result
import com.simranjeet.growise.presentation.viewmodels.auth.AuthViewModel
import com.simranjeet.growise.presentation.viewmodelfactory.auth.AuthViewModelFactory
import com.simranjeet.growise.presentation.ui.theme.black
import com.simranjeet.growise.presentation.ui.theme.darkGray
import com.simranjeet.growise.presentation.ui.theme.darkPurple
import com.simranjeet.growise.presentation.ui.theme.purple
import org.kodein.di.instance

enum class AuthScreen {
    Signup,
    Login
}

@Composable
fun AuthNavigator() {
    var currentScreen by remember { mutableStateOf(AuthScreen.Signup) }
    when (currentScreen) {
        AuthScreen.Signup -> {
            RegisterScreen(onNavigateToLogin = { currentScreen = AuthScreen.Login })
        }
        AuthScreen.Login -> {
            LoginScreen(onNavigateBack = { currentScreen = AuthScreen.Signup })
        }
    }
}

@Composable
fun RegisterScreen(onNavigateToLogin: () -> Unit) {
    val factory: AuthViewModelFactory by DIContainer.di.instance()
    val viewModel: AuthViewModel = viewModel(factory = factory)

    val context = LocalContext.current

    val authState by viewModel.authState.collectAsState()

    var emailValue by remember { mutableStateOf("") }
    var passwordValue by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(black),
        contentAlignment = Alignment.TopCenter
    ) {
        Gradient()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(top = 110.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RegisterHeader()

            Spacer(modifier = Modifier.height(40.dp))

            GoogleSignInButton(
                onClick = { viewModel.signInWithGoogle() }
            )

            when (authState) {
                is Result.Loading -> {}

                is Result.Success -> {
                    Toast.makeText(context, "Sign Up Success", Toast.LENGTH_SHORT).show()
                    LoginScreen(onNavigateToLogin)
                }

                is Result.Error -> {
                    Text(
                        text = "Error: ${(authState as Result.Error).message}",
                        color = Color.Red
                    )
                }

                null -> {}
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 30.dp)
            ) {
                Box(
                    modifier = Modifier.weight(1f).height(1.dp)
                        .background(Color.White.copy(alpha = 0.2f))
                )
                Text(
                    "Or",
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
                Box(
                    modifier = Modifier.weight(1f).height(1.dp)
                        .background(Color.White.copy(alpha = 0.2f))
                )
            }

            Column(horizontalAlignment = Alignment.Start) {
                Text("Email", color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                TextField(
                    value = emailValue,
                    onValueChange = { emailValue = it },
                    placeholder = {
                        Text(
                            "john.doe@example.com",
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = darkGray,
                        unfocusedContainerColor = darkGray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(horizontalAlignment = Alignment.Start) {
                Text("Password", color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                TextField(
                    value = passwordValue,
                    onValueChange = { passwordValue = it },
                    placeholder = {
                        Text(
                            "Enter your password",
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = darkGray,
                        unfocusedContainerColor = darkGray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(35.dp))

            Button(
                onClick = { viewModel.signUp(emailValue, passwordValue) },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = darkGray),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign up", modifier = Modifier.padding(vertical = 4.dp))
            }

            Spacer(modifier = Modifier.height(25.dp))

            TextButton(onClick =  onNavigateToLogin ) {
                Text(
                    buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Light,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        ) { append("Already have an account? ") }
                        withStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        ) { append("Log in") }
                    }
                )
            }
        }
    }
}


@Composable
private fun RegisterHeader() {
    Text(
        text = "Create An Account",
        style = MaterialTheme.typography.titleLarge,
        color = Color.White,
        fontWeight = FontWeight.Bold
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = "Enter your personal data to create an account",
        style = MaterialTheme.typography.bodyMedium,
        color = Color.White
    )
}


@Composable
fun Gradient() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.35f)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        purple,
                        darkPurple,
                        black
                    )
                )
            )
    )
}