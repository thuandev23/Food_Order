package com.example.food_ordering.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.food_ordering.model.AllVoucher
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class VoucherRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val databaseReference: DatabaseReference = database.reference

    fun getVouchers(): LiveData<List<AllVoucher>> {
        val userId = auth.currentUser?.uid ?: ""
        val vouchersLiveData = MutableLiveData<List<AllVoucher>>()
        val voucherRef: DatabaseReference = databaseReference.child("accounts").child("users").child(userId).child("MyVouchers")

        voucherRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val validVouchers = mutableListOf<AllVoucher>()
                val expiredVouchers = mutableListOf<AllVoucher>()
                for (voucherSnapshot in snapshot.children) {
                    val voucherItem = voucherSnapshot.getValue(AllVoucher::class.java)
                    voucherItem?.let {
                        if (isVoucherExpired(it.expiryDate ?: "")) {
                            expiredVouchers.add(it)
                        } else {
                            validVouchers.add(it)
                        }
                    }
                }
                vouchersLiveData.value = validVouchers + expiredVouchers
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
        return vouchersLiveData
    }

    private fun isVoucherExpired(expiryDate: String): Boolean {
        val currentDateString = LocalDate.now().format(DateTimeFormatter.ISO_DATE)
        return expiryDate < currentDateString
    }
}
