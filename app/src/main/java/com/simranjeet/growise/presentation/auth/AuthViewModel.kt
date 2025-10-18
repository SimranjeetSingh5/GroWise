package com.simranjeet.growise.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simranjeet.growise.data.model.AuthResponse
import com.simranjeet.growise.domain.usecase.auth.GoogleSignInUseCase
import com.simranjeet.growise.domain.usecase.auth.SignInUseCase
import com.simranjeet.growise.domain.usecase.auth.SignUpUseCase
import com.simranjeet.growise.domain.usecase.bases.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val signUpUseCase: SignUpUseCase,
    private val signInUseCase: SignInUseCase,
    private val googleSignInUseCase: GoogleSignInUseCase
) : ViewModel() {
    private var signUpJob: Job? = null
    private var signInJob: Job? = null
    private var googleSignInJob: Job? = null

    private val _authState = MutableStateFlow<Result<AuthResponse>>(Result.Loading)
    val authState: StateFlow<Result<AuthResponse>> = _authState

    fun signUp(email: String, password: String) {
        signUpJob?.cancel()
        viewModelScope.launch(Dispatchers.IO) { signUpUseCase.execute(email to password) }
        signUpJob =
            viewModelScope.launch { signUpUseCase.resultFlow.collect { _authState.value = it } }
    }

    fun signIn(email: String, password: String) {
        signInJob?.cancel()
        viewModelScope.launch(Dispatchers.IO) { signInUseCase.execute(email to password) }
        signInJob =
            viewModelScope.launch { signInUseCase.resultFlow.collect { _authState.value = it } }
    }

    fun signInWithGoogle() {
        googleSignInJob?.cancel()
        viewModelScope.launch(Dispatchers.IO) { googleSignInUseCase.execute() }
        googleSignInJob = viewModelScope.launch {
            googleSignInUseCase.resultFlow.collect {
                _authState.value = it
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        signUpJob?.cancel()
        signInJob?.cancel()
        googleSignInJob?.cancel()
    }
}