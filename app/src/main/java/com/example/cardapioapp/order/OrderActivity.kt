package com.example.cardapioapp.order

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cardapioapp.R
import com.example.cardapioapp.menu.MenuActivity
import com.example.cardapioapp.databinding.ActivityOrderBinding
import kotlinx.coroutines.launch

class OrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderBinding
    private val orderViewModel: OrderViewModel by viewModels()
    private lateinit var adapter: OrderAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)
        enableEdgeToEdge()

        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupUI()
        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            orderViewModel.orderedItems.collect { items ->
                adapter.setItems(items)
            }
        }

        lifecycleScope.launch {
            orderViewModel.errorMessage.collect { message ->
                showErrorDialog(message)
            }
        }

        lifecycleScope.launch {
            orderViewModel.navigateToMenuScreen.collect {
                navigateToMenuScreen()
            }
        }

        lifecycleScope.launch {
            orderViewModel.totalPrice.collect { totalPrice ->
                binding.textView9.text = "R$${"%.2f".format(totalPrice)}"
            }
        }

        lifecycleScope.launch {
            orderViewModel.succesMessage.collect { message ->
                showSuccessDialog(message)
            }
        }
    }

    private fun setupUI() {
        binding.btnVoltar.setOnClickListener {
            orderViewModel.returnToMenuScreen()
        }

        binding.buttonFinalizar.setOnClickListener {
            orderViewModel.finalizeOrder()
        }
    }

    private fun setupRecyclerView() {
        adapter = OrderAdapter(orderViewModel)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@OrderActivity)
            adapter = this@OrderActivity.adapter
        }
    }

    private fun navigateToMenuScreen() {
        finish()
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Erro ao realizar pedido")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showSuccessDialog(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
        orderViewModel.returnToMenuScreen()
    }

    override fun onResume() {
        super.onResume()
        orderViewModel.loadOrderedItems()
    }
}
