package com.example.cardapioapp.activities

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

//    fun login(email: String, password: String) {
//        viewModelScope.launch {
//            if (email.isEmpty() || password.isEmpty() || email.isBlank() || password.isBlank()) {
//                _errorMessage.emit("Por favor, preencha todos os campos.")
//            } else if (!validateCredentials(email, password)) {
//                _errorMessage.emit("Email ou senha incorretos.")
//            } else {
//                viewModelScope.launch {
//                    _navigateToNextScreen.emit(Unit)
//                }
//            }
//        }
//    }

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
                            _navigateToNextScreen.emit(Unit) // Navegar para a próxima tela
                        } else {
                            val errorMessage = "Erro ao fazer login. Verifique seu e-mail e senha."
//                            val errorMessage = task. exception?.message
                            _errorMessage.emit(errorMessage) // Emitir mensagem de erro
                        }
                    }
                }
        }
    }

//    fun logout(){
//        FirebaseAuth.getInstance().signOut()
//    }
//
//    fun authenticatedUserEmail(): String {
//        val firebaseAuth = FirebaseAuth.getInstance()
//        return firebaseAuth. currentUser?.email.toString()
//    }
//
//    fun authenticatedUserId(): String {
//        val firebaseAuth = FirebaseAuth.getInstance()
//        return firebaseAuth. currentUser?.uid.toString()
//    }

//    private fun validateCredentials(email: String, password: String): Boolean {
//        return true
//    }

    fun navigateToRegisterScreen() {
        viewModelScope.launch {
            _navigateToRegisterScreen.emit(Unit)
        }
    }
}