package com.example.cardapioapp.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class RecoveringPasswordViewModel : ViewModel(){

    private val _infoMessage = MutableSharedFlow<String>()
    val infoMessage = _infoMessage.asSharedFlow()

    fun recoveryPassword(email: String) {
        if(!validateEmailFormat(email)) {
            viewModelScope.launch {
                _infoMessage.emit("Digite o e-mail corretamente")
            }
        }
        else {
            //recuperar a senha
            viewModelScope.launch {
                _infoMessage.emit("Verifique as instruções enviadas no seu e-mail.")
            }
        }
    }

    private fun validateEmailFormat(email: String): Boolean {
        val emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
        return email.matches(emailPattern.toRegex())
    }
}