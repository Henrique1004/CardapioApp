package com.example.cardapioapp.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()
    private val _navigateToNextScreen = MutableSharedFlow<Unit>()
    val navigateToNextScreen = _navigateToNextScreen.asSharedFlow()
    private val _navigateToRegisterScreen = MutableSharedFlow<Unit>()
    val navigateToRegisterScreen = _navigateToRegisterScreen.asSharedFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            if (email.isEmpty() || password.isEmpty() || email.isBlank() || password.isBlank()) {
                _errorMessage.emit("Por favor, preencha todos os campos.")
            } else if (!validateCredentials(email, password)) {
                _errorMessage.emit("Email ou senha incorretos.")
            } else {
                viewModelScope.launch {
                    _navigateToNextScreen.emit(Unit)
                }
            }
        }
    }

    private fun validateCredentials(email: String, password: String): Boolean {
        return true
    }

    fun navigateToRegisterScreen() {
        viewModelScope.launch {
            _navigateToRegisterScreen.emit(Unit)
        }
    }
}