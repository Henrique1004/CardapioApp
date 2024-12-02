package com.example.cardapioapp.register

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.cardapioapp.R
import com.example.cardapioapp.databinding.ActivityRegisterBinding
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_register)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            registerViewModel.infoMessage.collect { message ->
                showInfoDialog(message)
            }
        }

        lifecycleScope.launch {
            registerViewModel.navigateToLoginScreen.collect {
                finish()
            }
        }
    }

    private fun setupUI() {
        val logoImageUrl = "https://firebasestorage.googleapis.com/v0/b/cardapio-app-e4654.firebasestorage.app/o/logos%2Flogo.jpg?alt=media&token=71b19376-a7c2-40c3-8d62-4db3963ca68b"

        Glide.with(binding.imageView4.context)
            .load(logoImageUrl)
            .apply(RequestOptions.circleCropTransform())
            .placeholder(R.drawable.loading) // Imagem de carregamento
            .error(R.drawable.baseline_error_24) // Imagem de erro
            .into(binding.imageView4)

        binding.regButton.setOnClickListener {
            val name = binding.editTextNome.text.toString()
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextSenha.text.toString()
            val passwordConfirmation = binding.editTextConfSenha.text.toString()

            registerViewModel.register(name, email, password, passwordConfirmation)
        }

        binding.returnButton.setOnClickListener {
            registerViewModel.returnToLoginScreen()
        }
    }

    private fun showInfoDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Aviso")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}
