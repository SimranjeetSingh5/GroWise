package com.simranjeet.growise.presentation.viewmodels.auth

import android.util.Log
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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val signUpUseCase: SignUpUseCase,
    private val signInUseCase: SignInUseCase,
    private val googleSignInUseCase: GoogleSignInUseCase
) : ViewModel() {
    private var signUpJob: Job? = null
    private var signInJob: Job? = null
    private var googleSignInJob: Job? = null

    private val _authState = MutableStateFlow<Result<Unit>?>(null)
    val authState: StateFlow<Result<Unit>?> = _authState.asStateFlow()

    init {
        viewModelScope.launch { signInUseCase.resultFlow.collect { result -> _authState.value = result } }
    }
    fun signUp(email: String, password: String) {
        signUpJob?.cancel()
        viewModelScope.launch(Dispatchers.IO) { signUpUseCase.execute(email to password) }
        signUpJob =
            viewModelScope.launch { signUpUseCase.resultFlow.collect { _authState.value = it } }
    }
    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            signInUseCase.execute(email to password)
        }
    }


    fun clearAuthState() {
        _authState.value = null
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