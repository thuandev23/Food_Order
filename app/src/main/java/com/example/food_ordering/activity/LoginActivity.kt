package com.example.food_ordering.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.food_ordering.R
import com.example.food_ordering.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class LoginActivity : AppCompatActivity() {
    //firebase
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient
    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = Firebase.auth
        database = Firebase.database.reference

        // initialize Google SignIn
        val googleSignInOption = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOption)

        binding.btnLogin.setOnClickListener{
            email = binding.email.text.toString().trim()
            password = binding.password.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Please fill email !", Toast.LENGTH_SHORT).show()
            } else if (email.isEmpty()) {
                Toast.makeText(this, "Please fill password !", Toast.LENGTH_SHORT).show()
            } else  {
                loginUser(email, password)
            }
        }
        binding.btnGoogle.setOnClickListener {
            val signIntent = googleSignInClient.signInIntent
            launcher.launch(signIntent)
        }

        binding.txtDontHaveAcc.setOnClickListener{
            startActivity(Intent(this, SignActivity::class.java))
        }

        var isPasswordVisible = false
        binding.showPassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            togglePasswordVisibility(isPasswordVisible)
        }
    }
    private fun togglePasswordVisibility(passwordVisible: Boolean) {
        if (passwordVisible){
            binding.password.transformationMethod = null
        }
        else{
            binding.password.transformationMethod = PasswordTransformationMethod.getInstance()
        }
    }
    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                checkUserAddress()
            } else {
                Toast.makeText(
                    this,
                    "Authentication failed: ${task.exception?.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun checkUserAddress() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            database.child("accounts").child("users").child(userId).child("address").get().addOnSuccessListener { snapshot ->
                val address = snapshot.value as? String
                if (address.isNullOrEmpty()) {
                    startActivity(Intent(this, ChooseLocationActivity::class.java))
                } else {
                    startActivity(Intent(this, MainActivity::class.java))
                }
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to check address: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser!=null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
    // Launcher for google signIn
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if (task.isSuccessful) {
                val account: GoogleSignInAccount? = task.result
                val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
                auth.signInWithCredential(credential).addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        //successfully signIn google
                        Toast.makeText(
                            this,
                            "Successfully SignIn With Google",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Google SignIn failed: ${authTask.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Google SignIn failed:${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}