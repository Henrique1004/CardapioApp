package com.example.cardapioapp.productdetails


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProductDetailsViewModel : ViewModel() {
    private val _infoMessage = MutableSharedFlow<String>()
    val infoMessage = _infoMessage.asSharedFlow()
    private val _navigateToMenuScreen = MutableSharedFlow<Unit>()
    val navigateToMenuScreen = _navigateToMenuScreen.asSharedFlow()
    private val db = FirebaseFirestore.getInstance()
    private val userEmail = FirebaseAuth.getInstance().currentUser?.email
    private val ordersRef = db.collection("orders")


    private suspend fun createOrder(): Int {
        if (userEmail.isNullOrEmpty()) {
            _infoMessage.emit("Usuário não está logado")
            return 0
        }

        val querySnapshot = ordersRef.whereEqualTo("client", userEmail).get().await()

        if (querySnapshot.isEmpty) {
            var orderNumber = 1
            val lastOrderQuery = ordersRef.orderBy("order_number", Query.Direction.DESCENDING).limit(1)
            val lastOrderSnapshot = lastOrderQuery.get().await()

            if (lastOrderSnapshot.documents.isNotEmpty()) {
                val lastOrder = lastOrderSnapshot.documents.first()
                orderNumber = ((lastOrder.getLong("order_number") ?: 0) + 1).toInt()
            }
            val orderData = mapOf(
                "client" to userEmail,
                "products" to emptyList<Map<String, Any>>(),
                "order_number" to orderNumber.toLong()
            )
            val orderDocRef = ordersRef.document("order_$orderNumber")
            orderDocRef.set(orderData).await()
            return orderNumber
        } else {
            val document = querySnapshot.documents.first()
            val orderNumber = document.getLong("order_number") ?: 0
            return orderNumber.toInt()
        }
    }

    suspend fun addProductToOrder(productName: String, productPrice: Double) {
        try {
            if (userEmail.isNullOrEmpty()) {
                _infoMessage.emit("Usuário não está logado")
                return
            }

            val orderNumber = createOrder()

            if (orderNumber == 0) {
                _infoMessage.emit("Erro ao criar pedido.")
                return
            }

            val orderDocRef = ordersRef.document("order_$orderNumber")
            orderDocRef.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val existingProducts = documentSnapshot.get("products") as? List<Map<String, Any>> ?: emptyList()

                    val productExists = existingProducts.any { it["name"] == productName }

                    if (productExists) {
                        viewModelScope.launch {
                            _infoMessage.emit("Produto já está no pedido!")
                        }
                    } else {
                        val productMap = mapOf(
                            "name" to productName,
                            "price" to productPrice,
                            "quantity" to 1
                        )

                        orderDocRef.update("products", FieldValue.arrayUnion(productMap))
                            .addOnSuccessListener {
                                viewModelScope.launch {
                                    _infoMessage.emit("Produto adicionado ao pedido!")
                                }
                            }
                            .addOnFailureListener { exception ->
                                viewModelScope.launch {
                                    _infoMessage.emit("Erro ao adicionar produto: ${exception.message}")
                                }
                            }
                    }
                } else {
                    viewModelScope.launch {
                        _infoMessage.emit("Pedido não encontrado")
                    }
                }
            }
        } catch (e: Exception) {
            _infoMessage.emit("Erro inesperado: ${e.message}")
        }
    }

    fun returnToMenuScreen() {
        viewModelScope.launch {
            _navigateToMenuScreen.emit(Unit)
        }
    }
}
