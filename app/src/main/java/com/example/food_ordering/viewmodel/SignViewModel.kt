package com.example.food_ordering.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.food_ordering.R
import com.example.food_ordering.model.UserModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignViewModel(application: Application) : AndroidViewModel(application) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val context = getApplication<Application>().applicationContext

    val signUpStatus = MutableLiveData<Boolean>()
    val googleSignInStatus = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()

    fun createNewAccount(username: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, context.getString(R.string.account_created_successfully), Toast.LENGTH_SHORT).show()
                saveUserData(username, email, password)
                signUpStatus.value = true
            } else {
                errorMessage.value = "Account creation failed: ${task.exception?.message}"
            }
        }
    }

    private fun saveUserData(username: String, email: String, password: String) {
        val user = UserModel(username, email, password, "", "", "")
        val userId = auth.currentUser!!.uid
        database.child("accounts").child("users").child(userId).setValue(user)
    }

    fun handleGoogleSignInResult(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener { authTask ->
            if (authTask.isSuccessful) {
                saveUserData(account?.displayName ?: "", account?.email ?: "", "")
                googleSignInStatus.value = true
            } else {
                errorMessage.value = "Google SignIn failed: ${authTask.exception?.message}"
            }
        }
    }
}
