package com.example.food_ordering.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.food_ordering.R
import com.example.food_ordering.databinding.ActivityLoginBinding
import com.example.food_ordering.viewmodel.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class LoginActivity : AppCompatActivity() {
    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    private val viewModel: LoginViewModel by viewModels()

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Initialize Google SignIn
        val googleSignInOption = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOption)

        binding.btnLogin.setOnClickListener {
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, getString(R.string.please_fill_email), Toast.LENGTH_SHORT)
                    .show()
            } else if (password.isEmpty()) {
                Toast.makeText(this, getString(R.string.please_fill_password), Toast.LENGTH_SHORT)
                    .show()
            } else {
                viewModel.loginUser(email, password)
            }
        }

        binding.btnGoogle.setOnClickListener {
            val signIntent = googleSignInClient.signInIntent
            launcher.launch(signIntent)
        }

        binding.txtDontHaveAcc.setOnClickListener {
            startActivity(Intent(this, SignActivity::class.java))
        }

        var isPasswordVisible = false
        binding.showPassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            togglePasswordVisibility(isPasswordVisible)
        }

        viewModel.loginStatus.observe(this, Observer { success ->
            if (success) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                startActivity(Intent(this, ChooseLocationActivity::class.java))
            }
            finish()
        })

        viewModel.googleSignInStatus.observe(this, Observer { success ->
            if (success) {
                Toast.makeText(this, getString(R.string.successfully_signin_with_google), Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        })

        viewModel.errorMessage.observe(this, Observer { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        })
    }

    private fun togglePasswordVisibility(passwordVisible: Boolean) {
        if (passwordVisible) {
            binding.password.transformationMethod = null
        } else {
            binding.password.transformationMethod = PasswordTransformationMethod.getInstance()
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = viewModel.auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    // Launcher for Google SignIn
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if (task.isSuccessful) {
                viewModel.handleGoogleSignInResult(task.result)
            } else {
                Toast.makeText(this, "Google SignIn failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
