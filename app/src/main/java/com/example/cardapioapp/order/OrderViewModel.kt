package com.example.cardapioapp.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cardapioapp.productdetails.ProductDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class OrderViewModel : ViewModel() {

    private val _totalPrice = MutableSharedFlow<Double>()
    val totalPrice = _totalPrice.asSharedFlow()
    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()
    private val _successMessage = MutableSharedFlow<String>()
    val succesMessage = _successMessage.asSharedFlow()
    private val _navigateToMenuScreen = MutableSharedFlow<Unit>()
    val navigateToMenuScreen = _navigateToMenuScreen.asSharedFlow()
    private val _orderedItems = MutableSharedFlow<List<ProductDTO>>()
    val orderedItems = _orderedItems.asSharedFlow()
    private val db = FirebaseFirestore.getInstance()
    private val userEmail = FirebaseAuth.getInstance().currentUser?.email
    private val ordersRef = db.collection("orders")

    fun loadOrderedItems() {
        viewModelScope.launch {
            try {
                val querySnapshot = ordersRef.whereEqualTo("client", userEmail).get().await()
                val orderedItems = mutableListOf<ProductDTO>()
                var totalPrice = 0.0

                for (document in querySnapshot.documents) {
                    val products = document.get("products") as? List<Map<String, Any>> ?: emptyList()

                    products.forEach { productMap ->
                        val name = productMap["name"] as? String
                        val price = (productMap["price"] as? Number)?.toDouble()
                        val quantity = (productMap["quantity"] as? Number)?.toInt()

                        if (name != null && price != null && quantity != null) {
                            totalPrice += price * quantity
                            orderedItems.add(ProductDTO(name, price, quantity))
                        }
                    }
                }
                val sortedItems = orderedItems.sortedBy { it.name }
                _orderedItems.emit(sortedItems)
                _totalPrice.emit(totalPrice)
            } catch (e: Exception) {
                _errorMessage.emit("Erro inesperado: ${e.message}")
            }
        }
    }


    fun alterItemQuantity(productNameToUpdate: String, action: Int) {
        viewModelScope.launch {
            try {
                val querySnapshot = ordersRef.whereEqualTo("client", userEmail).get().await()
                for (document in querySnapshot.documents) {
                    val products = document.get("products") as? List<Map<String, Any>> ?: emptyList()

                    products.forEach { productMap ->
                        val name = productMap["name"] as String
                        val price = (productMap["price"] as Number).toDouble()
                        val quantity = (productMap["quantity"] as Number).toInt()

                        var newQuantity = 0

                        if(action == 1) {
                            newQuantity = quantity + 1
                        } else if (action == 2) {
                            if (quantity > 1) {
                                newQuantity = quantity - 1
                            }
                        }

                        if (productMap["name"] == productNameToUpdate) {
                            document.reference.update("products", FieldValue.arrayRemove(productMap))
                                .addOnSuccessListener {
                                    val updatedProduct = productMap.toMutableMap()
                                    updatedProduct["name"] = name
                                    updatedProduct["price"] = price
                                    updatedProduct["quantity"] = newQuantity

                                    document.reference.update("products", FieldValue.arrayUnion(updatedProduct))
                                        .addOnSuccessListener {
                                            loadOrderedItems()
                                        }
                                }
                        }
                    }
                }
            } catch (e: Exception) {
                _errorMessage.emit("Não foi possível alterar a quantidade do produto")
            }
        }
    }

    fun removeItem(itemName: String) {
        viewModelScope.launch {
            try {
                val querySnapshot = ordersRef.whereEqualTo("client", userEmail).get().await()
                for (document in querySnapshot.documents) {
                    val products = document.get("products") as? List<Map<String, Any>> ?: emptyList()

                    products.forEach { productMap ->
                        if (productMap["name"] == itemName) {
                            document.reference.update("products", FieldValue.arrayRemove(productMap))
                                .addOnSuccessListener {
                                    loadOrderedItems()
                                }
                        }
                    }
                }
            } catch (e: Exception) {
                _errorMessage.emit("Não foi possível remover produto")
            }
        }
    }

    fun returnToMenuScreen() {
        viewModelScope.launch {
            _navigateToMenuScreen.emit(Unit)
        }
    }

    fun finalizeOrder() {
        viewModelScope.launch {
            _successMessage.emit("Pedido realizado com sucesso!")
        }
    }
}
