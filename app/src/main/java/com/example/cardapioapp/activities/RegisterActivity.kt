package com.example.cardapioapp.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
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

        lifecycleScope.launch {
            registerViewModel.infoMessage.collect { message ->
                showInfoDialog(message)
            }
        }

        binding.regButton.setOnClickListener {
            val name = binding.editTextNome.text.toString()
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextSenha.text.toString()
            val passwordConfirmation = binding.editTextConfSenha.text.toString()

            registerViewModel.register(name, email, password, passwordConfirmation)
        }

        binding.returnButton.setOnClickListener {
            finish()
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
