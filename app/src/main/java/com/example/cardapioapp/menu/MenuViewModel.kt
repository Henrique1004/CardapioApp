package com.example.cardapioapp.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MenuViewModel : ViewModel() {

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()
    private val _navigateToLoginScreen = MutableSharedFlow<Unit>()
    val navigateToLoginScreen = _navigateToLoginScreen.asSharedFlow()
    private val _navigateToOrderScreen = MutableSharedFlow<Unit>()
    val navigateToOrderScreen = _navigateToOrderScreen.asSharedFlow()
    private val _menuItems = MutableSharedFlow<List<MenuItem>>()
    val menuItems = _menuItems.asSharedFlow()
    private val db = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()


    fun logout(){
        val userEmail = firebaseAuth.currentUser?.email.toString()
        if (userEmail.isNotEmpty()) {
            val ordersRef = db.collection("orders")
            ordersRef.whereEqualTo("client", userEmail)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents) {
                        document.reference.delete()
                    }
                }
        }
        db.clearPersistence()
        firebaseAuth.signOut()
        navigateToLoginScreen()
    }

    private fun navigateToLoginScreen() {
        viewModelScope.launch {
            _navigateToLoginScreen.emit(Unit)
        }
    }


    fun loadItems() {
        viewModelScope.launch {
            try {
                val querySnapshot = db.collection("products").get().await()

                val items = mutableListOf<MenuItem>()

                for (document in querySnapshot.documents) {
                    val section = document.getString("section") ?: "Sem seção"
                    val productList = document.get("items") as? List<Map<String, Any>> ?: emptyList()

                    items.add(MenuItem.Section(section))

                    productList.forEach { productMap ->
                        val name = productMap["name"] as? String
                        val description = productMap["description"] as? String
                        val price = (productMap["price"] as? Number)?.toDouble()
                        val imageUrl = productMap["imageUrl"] as? String

                        if (name != null && description != null && price != null && imageUrl != null) {
                            items.add(MenuItem.Product(name, description, price, imageUrl))
                        }
                    }
                }
                _menuItems.emit(items)
            } catch (e: Exception) {
                _errorMessage.emit(e.message ?: "Erro ao carregar o cardápio")
            }
        }
    }

    fun navigateToOrderScreen() {
        viewModelScope.launch {
            _navigateToOrderScreen.emit(Unit)
        }
    }
}
