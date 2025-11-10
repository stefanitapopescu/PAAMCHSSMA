package com.example.paamchssma.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.paamchssma.R
import com.example.paamchssma.campus.CampusMapActivity
import com.example.paamchssma.databinding.ActivitySignUpBinding
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            binding = ActivitySignUpBinding.inflate(layoutInflater)
            setContentView(binding.root)

            // Back button
            binding.backButton.setOnClickListener {
                finish()
            }

            // Sign up button
            binding.signUpButton.setOnClickListener {
                attemptSignUp()
            }

            // Login link
            binding.loginTextView.setOnClickListener {
                finish() // Înapoi la LoginActivity
            }

        } catch (e: Exception) {
            Toast.makeText(this, "Eroare: ${e.message}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun attemptSignUp() {
        val lastName = binding.lastNameEditText.text.toString().trim()
        val firstName = binding.firstNameEditText.text.toString().trim()
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()
        val confirmPassword = binding.confirmPasswordEditText.text.toString().trim()

        // Validări
        if (lastName.isEmpty() || firstName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(R.string.signup_error_empty_fields), Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, getString(R.string.signup_error_password_mismatch), Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            Toast.makeText(this, getString(R.string.signup_error_password_short), Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)

        // Creare cont Firebase
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                // Actualizare profil cu nume și prenume
                val user = authResult.user
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName("$firstName $lastName")
                    .build()

                user?.updateProfile(profileUpdates)
                    ?.addOnSuccessListener {
                        Toast.makeText(this, getString(R.string.signup_success), Toast.LENGTH_SHORT).show()
                        navigateToCampus()
                    }
                    ?.addOnFailureListener { error ->
                        // Contul a fost creat, dar update-ul numelui a eșuat
                        // Mergem oricum înainte
                        Toast.makeText(this, "Cont creat cu succes!", Toast.LENGTH_SHORT).show()
                        navigateToCampus()
                    }
            }
            .addOnFailureListener { error ->
                setLoading(false)
                val errorMsg = when {
                    error.message?.contains("email address is already in use") == true ->
                        getString(R.string.signup_error_email_exists)
                    error.message?.contains("email address is badly formatted") == true ->
                        getString(R.string.signup_error_invalid_email)
                    else -> getString(R.string.signup_error_generic, error.message ?: "Eroare necunoscută")
                }
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
            }
    }

    private fun setLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.signUpButton.isEnabled = !loading
        binding.lastNameEditText.isEnabled = !loading
        binding.firstNameEditText.isEnabled = !loading
        binding.emailEditText.isEnabled = !loading
        binding.passwordEditText.isEnabled = !loading
        binding.confirmPasswordEditText.isEnabled = !loading
    }

    private fun navigateToCampus() {
        setLoading(false)
        startActivity(Intent(this, CampusMapActivity::class.java))
        finish()
    }
}

