package com.example.cardapioapp.login

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
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.cardapioapp.R
import com.example.cardapioapp.menu.MenuActivity
import com.example.cardapioapp.recoveringpassword.RecoveringPasswordActivity
import com.example.cardapioapp.register.RegisterActivity
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

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
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

        val logoImageUrl = "https://firebasestorage.googleapis.com/v0/b/cardapio-app-e4654.firebasestorage.app/o/logos%2Flogo.jpg?alt=media&token=71b19376-a7c2-40c3-8d62-4db3963ca68b"

        Glide.with(binding.imageView4.context)
            .load(logoImageUrl)
            .apply(RequestOptions.circleCropTransform())
            .placeholder(R.drawable.loading)
            .error(R.drawable.baseline_error_24)
            .into(binding.imageView4)


        binding.loginButton.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
            loginViewModel.login(email, password)
        }

        binding.regButton.setOnClickListener {
            loginViewModel.navigateToRegisterScreen()
        }
    }

    private fun observeViewModel() {
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

    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Erro no Login")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun navigateToNextScreen() {
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToRegisterScreen() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}
