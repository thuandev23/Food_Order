package com.example.food_ordering.view.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.food_ordering.view.adapter.MyVoucherAdapter
import com.example.food_ordering.databinding.ActivityMyVoucherBinding
import com.example.food_ordering.model.AllVoucher
import com.example.food_ordering.viewmodel.VoucherViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MyVoucherActivity : AppCompatActivity() {
    private val voucherViewModel : VoucherViewModel by viewModels()
    private lateinit var binding: ActivityMyVoucherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyVoucherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgBtnBack.setOnClickListener {
            finish()
        }
        binding.btnVoucherValidMore.setOnClickListener {
            if (binding.voucherValidRecyclerView.visibility == RecyclerView.VISIBLE) {
                binding.voucherValidRecyclerView.visibility = RecyclerView.GONE
            } else {
                binding.voucherValidRecyclerView.visibility = RecyclerView.VISIBLE
            }
        }
        binding.btnVoucherExpiredMore.setOnClickListener {
            if (binding.voucherExpiredRecyclerView.visibility == RecyclerView.VISIBLE) {
                binding.voucherExpiredRecyclerView.visibility = RecyclerView.GONE
            } else {
                binding.voucherExpiredRecyclerView.visibility = RecyclerView.VISIBLE
            }
        }

        setupObservers()

    }
    private fun setupObservers() {
        voucherViewModel.vouchers.observe(this) { vouchers ->
            val validVouchers = vouchers.filter { !isVoucherExpired(it.expiryDate ?: "") }
            val expiredVouchers = vouchers.filter { isVoucherExpired(it.expiryDate ?: "") }

            setAdapter(validVouchers, binding.voucherValidRecyclerView)
            setAdapter(expiredVouchers, binding.voucherExpiredRecyclerView)
        }
    }
    private fun isVoucherExpired(expiryDate: String): Boolean {
        val currentDateString = LocalDate.now().format(DateTimeFormatter.ISO_DATE)
        return expiryDate > currentDateString
    }
    private fun setAdapter(items: List<AllVoucher>, recyclerView: RecyclerView) {
        val adapter = MyVoucherAdapter(items, this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }
}