package com.example.cardapioapp.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val _infoMessage = MutableSharedFlow<String>()
    val infoMessage = _infoMessage.asSharedFlow()
    private val _navigateToNextScreen = MutableSharedFlow<Unit>()

    fun register(name: String, email: String, password: String, passwordConfirmation: String) {
        viewModelScope.launch {
            // Simulação da lógica de validação
            if (name.isEmpty() || name.isBlank() || email.isEmpty() || email.isBlank() || password.isEmpty() || password.isBlank() || passwordConfirmation.isEmpty() || passwordConfirmation.isBlank()) {
                _infoMessage.emit("Por favor, preencha todos os campos.")
            } else if (!validateEmailFormat(email)) {
                _infoMessage.emit("Digite o e-mail corretamente.")
            } else if (!validatePasswordFields(password, passwordConfirmation)) {
                _infoMessage.emit("Os campos de senha não coincidem.")
            }
            else {
                //fazer o cadastro
                _infoMessage.emit("Cadastro feito com sucesso!")
            }
        }
    }

    private fun validateEmailFormat(email: String): Boolean {
        val emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
        return email.matches(emailPattern.toRegex())
    }

    private fun validatePasswordFields(password: String, passwordConfirmation: String): Boolean {
        return password == passwordConfirmation
    }
}