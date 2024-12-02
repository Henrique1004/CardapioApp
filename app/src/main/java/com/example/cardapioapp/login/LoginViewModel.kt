package com.example.cardapioapp.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
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
        if (email.isEmpty() || password.isEmpty() || email.isBlank() || password.isBlank()) {
            viewModelScope.launch {
                _errorMessage.emit("Por favor, preencha todos os campos.")
            }
        } else {
            val firebaseAuth = FirebaseAuth.getInstance()
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    viewModelScope.launch {
                        if (task.isSuccessful) {
                            _navigateToNextScreen.emit(Unit)
                        } else {
                            val errorMessage = "Erro ao fazer login. Verifique seu e-mail e senha."
                            _errorMessage.emit(errorMessage)
                        }
                    }
                }
        }
    }

    fun navigateToRegisterScreen() {
        viewModelScope.launch {
            _navigateToRegisterScreen.emit(Unit)
        }
    }
}
