package com.example.cardapioapp.recoveringpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
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

            val firebaseAuth = FirebaseAuth.getInstance()
            firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        viewModelScope.launch {
                            _infoMessage.emit("Verifique as instruções enviadas no seu e-mail.")
                        }
                    } else {
                        viewModelScope.launch {
                            _infoMessage.emit("Digite o e-mail corretamente")
                        }
                    }
                }
        }
    }

    private fun validateEmailFormat(email: String): Boolean {
        val emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
        return email.matches(emailPattern.toRegex())
    }
}
