package com.example.cardapioapp.menu

sealed class MenuItem {
    data class Section(val title: String) : MenuItem()
    data class Product(val name: String, val description: String, val price: Double, val imageUrl: String) : MenuItem()
}
