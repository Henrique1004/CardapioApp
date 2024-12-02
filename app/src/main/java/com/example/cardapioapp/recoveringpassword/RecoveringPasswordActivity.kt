package com.example.cardapioapp.recoveringpassword

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.cardapioapp.R
import com.example.cardapioapp.databinding.ActivityRecoveringPasswordBinding
import kotlinx.coroutines.launch

class RecoveringPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecoveringPasswordBinding
    private val recoveringPasswordViewModel: RecoveringPasswordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recovering_password)
        enableEdgeToEdge()

        binding = ActivityRecoveringPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            recoveringPasswordViewModel.infoMessage.collect { message ->
                showInfoDialog(message)
            }
        }    }

    private fun setupUI() {
        binding.sendButton.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            recoveringPasswordViewModel.recoveryPassword(email)
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
