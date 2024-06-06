package com.example.food_ordering.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.food_ordering.model.OrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TransactionHistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val context = getApplication<Application>().applicationContext

    val transactionHistory = MutableLiveData<List<OrderDetails>>()
    val errorMessage = MutableLiveData<String>()

    fun fetchTransactionHistory() {
        val userId = auth.currentUser?.uid ?: ""
        database.reference.child("accounts").child("users").child(userId).child("TransactionHistory")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val transactionList = mutableListOf<OrderDetails>()
                    for (data in snapshot.children) {
                        val orderDetails = data.getValue(OrderDetails::class.java)
                        orderDetails?.let { transactionList.add(it) }
                    }
                    transactionHistory.value = transactionList
                }
                override fun onCancelled(error: DatabaseError) {
                    errorMessage.value = "Failed to fetch transaction history: ${error.message}"
                }
            })
    }
}
