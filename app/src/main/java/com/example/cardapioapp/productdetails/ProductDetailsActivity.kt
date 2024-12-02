package com.example.cardapioapp.productdetails

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.cardapioapp.R
import com.example.cardapioapp.databinding.ActivityProductDetailsBinding
import kotlinx.coroutines.launch

class ProductDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailsBinding
    private val productDetailsViewModel: ProductDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)
        enableEdgeToEdge()

        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        val productName = intent.getStringExtra("productName") ?: "Nome não disponível"
        val productDescription = intent.getStringExtra("productDescription") ?: "Descrição não disponível"
        val productPrice = intent.getDoubleExtra("productPrice", 0.0)
        val productImageUrl = intent.getStringExtra("productImageUrl") ?: ""

        binding.productName.text = productName
        binding.productDescription.text = productDescription
        binding.productPrice.text = "R$${"%.2f".format(productPrice)}"

        Glide.with(binding.imageView4.context)
            .load(productImageUrl)
            .apply(RequestOptions().transform(RoundedCorners(32)))
            .placeholder(R.drawable.loading)
            .error(R.drawable.baseline_error_24)
            .into(binding.imageView4)

        binding.addButton.setOnClickListener {
            lifecycleScope.launch {
                productDetailsViewModel.addProductToOrder(productName, productPrice)
            }
        }

        binding.returnButton.setOnClickListener {
            productDetailsViewModel.returnToMenuScreen()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            productDetailsViewModel.infoMessage.collect { message ->
                showInfoDialog(message)
            }
        }

        lifecycleScope.launch {
            productDetailsViewModel.navigateToMenuScreen.collect {
                finish()
            }
        }
    }

    private fun showInfoDialog(message: String) {
        if(message == "Produto adicionado ao pedido!") {
            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
        }
        else {
            AlertDialog.Builder(this)
                .setTitle("Aviso")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show()
        }
    }
}
