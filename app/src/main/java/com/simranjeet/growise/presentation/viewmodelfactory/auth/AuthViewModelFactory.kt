package com.simranjeet.growise.presentation.viewmodelfactory.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.simranjeet.growise.data.repository.UserRepository
import com.simranjeet.growise.domain.usecase.auth.GoogleSignInUseCase
import com.simranjeet.growise.domain.usecase.auth.SignInUseCase
import com.simranjeet.growise.domain.usecase.auth.SignUpUseCase
import com.simranjeet.growise.presentation.viewmodels.auth.AuthViewModel

class AuthViewModelFactory(
    private val signUpUseCase: SignUpUseCase,
    private val signInUseCase: SignInUseCase,
    private val googleSignInUseCase: GoogleSignInUseCase,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(signUpUseCase, signInUseCase, googleSignInUseCase,userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}