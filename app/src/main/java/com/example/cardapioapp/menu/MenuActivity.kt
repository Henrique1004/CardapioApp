package com.example.cardapioapp.menu

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cardapioapp.order.OrderActivity
import com.example.cardapioapp.productdetails.ProductDetailsActivity
import com.example.cardapioapp.databinding.ActivityMenuBinding
import com.example.cardapioapp.login.LoginActivity
import kotlinx.coroutines.launch

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding
    private val menuViewModel: MenuViewModel by viewModels()
    private lateinit var adapter: MenuAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupUI()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = MenuAdapter { product -> onListItemClicked(product) }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MenuActivity)
            adapter = this@MenuActivity.adapter
        }
    }

    private fun setupUI() {
        binding.logoutButton.setOnClickListener {
            menuViewModel.logout()
        }

        binding.orderButton.setOnClickListener {
            menuViewModel.navigateToOrderScreen()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            menuViewModel.navigateToLoginScreen.collect {
                navigateToLoginScreen()
            }
        }

        lifecycleScope.launch {
            menuViewModel.navigateToOrderScreen.collect {
                navigateToOrderScreen()
            }
        }

        lifecycleScope.launch {
            menuViewModel.menuItems.collect { items ->
                adapter.setItems(items)
            }
        }

        lifecycleScope.launch {
            menuViewModel.errorMessage.collect { message ->
                showErrorDialog(message)
            }
        }
    }

    private fun showErrorDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Erro")
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
    }

    private fun onListItemClicked(product: MenuItem.Product) {
        val intent = Intent(this, ProductDetailsActivity::class.java).apply {
            putExtra("productName", product.name)
            putExtra("productDescription", product.description)
            putExtra("productPrice", product.price)
            putExtra("productImageUrl", product.imageUrl)
        }
        startActivity(intent)
    }

    private fun navigateToLoginScreen() {
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun navigateToOrderScreen() {
        val intent = Intent(this, OrderActivity::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        menuViewModel.loadItems()
    }
}
