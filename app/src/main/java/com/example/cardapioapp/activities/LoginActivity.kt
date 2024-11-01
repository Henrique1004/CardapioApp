package com.example.cardapioapp.activities

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.cardapioapp.R
import com.example.cardapioapp.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val forgotPasswordTextView = binding.textViewEsqueceuSenha
        val text = binding.textViewEsqueceuSenha.text
        val spannableString = SpannableString(text)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@LoginActivity, RecoveringPasswordActivity::class.java)
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
            }
        }

        spannableString.setSpan(clickableSpan, 0, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        forgotPasswordTextView.text = spannableString
        forgotPasswordTextView.movementMethod = LinkMovementMethod.getInstance()

        lifecycleScope.launch {
            loginViewModel.errorMessage.collect { message ->
                showErrorDialog(message)
            }
        }

        lifecycleScope.launch {
            loginViewModel.navigateToNextScreen.collect {
                navigateToNextScreen()
            }
        }

        lifecycleScope.launch {
            loginViewModel.navigateToRegisterScreen.collect {
                navigateToRegisterScreen()
            }
        }

        binding.loginButton.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
            loginViewModel.login(email, password)
        }

        binding.regButton.setOnClickListener {
            loginViewModel.navigateToRegisterScreen()
        }
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Erro no Login")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun navigateToNextScreen() {
//        val intent = Intent(this, NextActivity::class.java)
//        startActivity(intent)
    }

    private fun navigateToRegisterScreen() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}
