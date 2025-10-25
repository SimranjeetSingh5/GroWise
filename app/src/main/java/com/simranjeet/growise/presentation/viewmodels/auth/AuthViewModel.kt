package com.simranjeet.growise.presentation.viewmodels.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simranjeet.growise.data.model.UserType
import com.simranjeet.growise.domain.usecase.auth.GoogleSignInUseCase
import com.simranjeet.growise.domain.usecase.auth.SignInUseCase
import com.simranjeet.growise.domain.usecase.auth.SignUpUseCase
import com.simranjeet.growise.domain.usecase.auth.SyncUserUseCase
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
    private val googleSignInUseCase: GoogleSignInUseCase,
    private val syncUserUseCase: SyncUserUseCase
) : ViewModel() {
    private var signUpJob: Job? = null
    private var signInJob: Job? = null
    private var googleSignInJob: Job? = null

    private val _authState = MutableStateFlow<Result<Unit>?>(null)
    val authState: StateFlow<Result<Unit>?> = _authState.asStateFlow()


    fun signUp(email: String, password: String) {
        signUpJob?.cancel()
        viewModelScope.launch(Dispatchers.IO) { signUpUseCase.execute(email to password) }
        signUpJob =
            viewModelScope.launch {
                signUpUseCase.resultFlow.collect { result ->
                    _authState.value = result
                    if (result is Result.Success) {
                        syncUserUseCase.execute(UserType.EMAIL_PASSWORD)
                    }
                }
            }
    }

    fun signIn(email: String, password: String) {
        signInJob?.cancel()
        viewModelScope.launch { signInUseCase.execute(email to password) }
        signInJob = viewModelScope.launch {
            signInUseCase.resultFlow.collect { result ->
                _authState.value = result
                if (result is Result.Success) {
                    syncUserUseCase.execute(UserType.EMAIL_PASSWORD)
                }
            }
        }
    }


    fun clearAuthState() {
        _authState.value = null
    }

    fun signInWithGoogle() {
        googleSignInJob?.cancel()
        viewModelScope.launch(Dispatchers.IO) { googleSignInUseCase.execute() }
        googleSignInJob = viewModelScope.launch {
            googleSignInUseCase.resultFlow.collect { result ->
                _authState.value = result
                if (result is Result.Success) {
                    syncUserUseCase.execute(UserType.GOOGLE)
                }
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