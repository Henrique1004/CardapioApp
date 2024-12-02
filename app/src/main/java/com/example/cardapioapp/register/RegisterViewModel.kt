package com.example.cardapioapp.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue

class RegisterViewModel : ViewModel() {

    private val _navigateToLoginScreen = MutableSharedFlow<Unit>()
    val navigateToLoginScreen = _navigateToLoginScreen.asSharedFlow()
    private val _infoMessage = MutableSharedFlow<String>()
    val infoMessage = _infoMessage.asSharedFlow()

    fun register(userName: String, userEmail: String, userPassword: String, userConfPassword: String) {
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        if(userEmail.isEmpty() || userEmail.isBlank() || userPassword.isEmpty() || userPassword.isBlank() || userConfPassword.isEmpty() || userConfPassword.isBlank() || userName.isEmpty() || userName.isBlank()) {
            viewModelScope.launch {
                _infoMessage.emit("Por favor, preencha todos os campos.")
            }
        }
        else if(!validateEmailFormat(userEmail)) {
            viewModelScope.launch {
                _infoMessage.emit("Por favor, preencha o e-mail corretamente.")
            }
        } else if (!validatePasswordFields(userPassword, userConfPassword)) {
            viewModelScope.launch {
                _infoMessage.emit("Os campos de senha não coincidem.")
            }
        } else if (!validatePasswordLength(userPassword)) {
            viewModelScope.launch {
                _infoMessage.emit("A senha deve ter no mínimo 6 caracteres.")
            }
        }
        else {
            val usersCollection = db.collection("users")
            usersCollection.whereEqualTo("email", userEmail)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        createUserInAuth(auth, userEmail, userPassword, userName)
                    } else {
                        viewModelScope.launch {
                            _infoMessage.emit("Usuário já cadastrado!")
                        }
                    }
                }
        }
    }

    private fun createUserInAuth(auth: FirebaseAuth, email: String, password: String, name: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = task.result?.user?.uid ?: return@addOnCompleteListener
                    viewModelScope.launch {
                        _infoMessage.emit("Cadastro feito com sucesso!")
                    }
                    createUserDocument(name)
                }
            }
    }

    private fun createUserDocument(userName: String) {
        val db = FirebaseFirestore.getInstance()
        val usersCollection = db.collection("users")

        usersCollection.orderBy("created", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                val newUserNumber = if (documents.isEmpty) {
                    1
                } else {
                    val lastUserName = documents.first().id
                    val lastUserNumber = lastUserName.split("_").last().toInt()
                    lastUserNumber + 1
                }

                val newUserDocumentId = "user_$newUserNumber"
                val createdAt = FieldValue.serverTimestamp()

                val userData = hashMapOf(
                    "name" to userName,
                    "created" to createdAt
                )

                usersCollection.document(newUserDocumentId).set(userData)
            }
    }


    private fun validateEmailFormat(email: String): Boolean {
        val emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
        return email.matches(emailPattern.toRegex())
    }

    private fun validatePasswordFields(password: String, passwordConfirmation: String): Boolean {
        return password == passwordConfirmation
    }

    private fun validatePasswordLength(password: String): Boolean {
        return password.length >= 6
    }

    fun returnToLoginScreen() {
        viewModelScope.launch {
            _navigateToLoginScreen.emit(Unit)
        }
    }
}
