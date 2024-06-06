package com.example.food_ordering.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.food_ordering.R
import com.example.food_ordering.view.activity.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference
    private val context = getApplication<Application>().applicationContext

    val loginStatus = MutableLiveData<Boolean>()
    val googleSignInStatus = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()

    fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                checkUserAddress()
            } else {
                errorMessage.value = context.getString(R.string.authentication_failed, task.exception?.message)
            }
        }
    }

    private fun checkUserAddress() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            database.child("accounts").child("users").child(userId).child("address").get().addOnSuccessListener { snapshot ->
                val address = snapshot.value as? String
                if (address.isNullOrEmpty()) {
                    loginStatus.value = false
                } else {
                    loginStatus.value = true
                }
            }.addOnFailureListener {
                errorMessage.value = "Failed to check address: ${it.message}"
            }
        }
    }

    fun handleGoogleSignInResult(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener { authTask ->
            if (authTask.isSuccessful) {
                googleSignInStatus.value = true
                Intent(context, MainActivity::class.java).also {
                    context.startActivity(it)
                }
            } else {
                errorMessage.value = "Google SignIn failed: ${authTask.exception?.message}"
            }
        }
    }
}
