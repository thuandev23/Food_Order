package com.example.food_ordering

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.food_ordering.adapter.MyVoucherAdapter
import com.example.food_ordering.databinding.ActivityMyVoucherBinding
import com.example.food_ordering.model.AllVoucher
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MyVoucherActivity : AppCompatActivity() {
    private lateinit var userId: String
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    private lateinit var validVoucherItems: MutableList<AllVoucher>
    private lateinit var expiredVoucherItems: MutableList<AllVoucher>

    private val binding: ActivityMyVoucherBinding by lazy {
        ActivityMyVoucherBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference()

        binding.imgBtnBack.setOnClickListener {
            finish()
        }

        validVoucherItems = mutableListOf()
        expiredVoucherItems = mutableListOf()

        retrieveVoucherItem()
    }

    private fun retrieveVoucherItem() {
        database = FirebaseDatabase.getInstance()
        userId = auth.currentUser?.uid ?: ""
        val voucherRef: DatabaseReference =
            database.reference.child("user").child(userId).child("MyVouchers")

        voucherRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                validVoucherItems.clear()
                expiredVoucherItems.clear()
                for (voucherSnapshot in snapshot.children) {
                    val voucherItem = voucherSnapshot.getValue(AllVoucher::class.java)
                    voucherItem?.let {
                        if (it.expiryDate?.let { it1 -> isVoucherExpired(it1) } == true) {
                            expiredVoucherItems.add(it)
                        } else {
                            validVoucherItems.add(it)
                        }
                    }
                }
                setAdapter(validVoucherItems, binding.voucherValidRecyclerView)
                setAdapter(expiredVoucherItems, binding.voucherExpiredRecyclerView)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("DatabaseError", "Error: ${error.message}")
            }

        })
    }
    private fun isVoucherExpired(expiryDate: String): Boolean {
        val currentDateString = LocalDate.now().format(DateTimeFormatter.ISO_DATE)
        return expiryDate < currentDateString
    }
    private fun setAdapter(items: List<AllVoucher>, recyclerView: RecyclerView) {
        val adapter = MyVoucherAdapter(items, this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }
}