package com.example.paamchssma.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.paamchssma.R
import com.example.paamchssma.campus.CampusMapActivity
import com.example.paamchssma.databinding.ActivityLoginBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            // LOGOUT FORȚAT la fiecare deschidere a aplicației
            try {
                auth.signOut()
                android.util.Log.d("LoginActivity", "🔒 Logout automat la pornire")
            } catch (e: Exception) {
                android.util.Log.e("LoginActivity", "Eroare logout: ${e.message}")
            }
            
            binding = ActivityLoginBinding.inflate(layoutInflater)
            setContentView(binding.root)

            // Login button
            binding.loginButton.setOnClickListener {
                attemptLogin()
            }

            // Sign up link
            binding.signUpTextView.setOnClickListener {
                startActivity(Intent(this, SignUpActivity::class.java))
            }
            
            // Skip button pentru test fără login
            binding.skipButton?.setOnClickListener {
                navigateToCampus()
            }
            
        } catch (e: Exception) {
            Toast.makeText(this, "Eroare login layout: ${e.message}", Toast.LENGTH_SHORT).show()
            navigateToCampus()
        }
    }

    private fun attemptLogin() {
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(R.string.login_error_empty_fields), Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                navigateToCampus()
            }
            .addOnFailureListener { error ->
                setLoading(false)
                val errorMsg = getString(R.string.login_error_generic, error.message ?: "Eroare necunoscută")
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
            }
    }

    private fun setLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.loginButton.isEnabled = !loading
        binding.emailEditText.isEnabled = !loading
        binding.passwordEditText.isEnabled = !loading
    }

    private fun navigateToCampus() {
        setLoading(false)
        startActivity(Intent(this, CampusMapActivity::class.java))
        finish()
    }
}
