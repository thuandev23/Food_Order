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
import com.example.food_ordering.databinding.ActivitySignBinding
import com.example.food_ordering.viewmodel.SignViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class SignActivity : AppCompatActivity() {

    private val binding: ActivitySignBinding by lazy {
        ActivitySignBinding.inflate(layoutInflater)
    }

    private val viewModel: SignViewModel by viewModels()
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Initialize Google SignIn
        val googleSignInOption = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOption)

        binding.btnCreateAcc.setOnClickListener {
            val username = binding.userName.text.toString()
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (username.isBlank()) {
                Toast.makeText(this, getString(R.string.please_fill_username), Toast.LENGTH_SHORT).show()
            } else if (email.isBlank()) {
                Toast.makeText(this, getString(R.string.please_fill_email), Toast.LENGTH_SHORT).show()
            } else if (password.isBlank()) {
                Toast.makeText(this, getString(R.string.please_fill_password), Toast.LENGTH_SHORT).show()
            } else {
                viewModel.createNewAccount(username, email, password)
            }
        }

        binding.txtHasAcc.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnGoogle.setOnClickListener {
            val signIntent = googleSignInClient.signInIntent
            launcher.launch(signIntent)
        }

        var isPasswordVisible = false
        binding.showPassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            togglePasswordVisibility(isPasswordVisible)
        }

        viewModel.signUpStatus.observe(this, Observer { success ->
            if (success) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        })

        viewModel.googleSignInStatus.observe(this, Observer { success ->
            if (success) {
                Toast.makeText(this, getString(R.string.successfully_signin_with_google), Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, ChooseLocationActivity::class.java))
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
