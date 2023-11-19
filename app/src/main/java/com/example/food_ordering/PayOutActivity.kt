package com.example.food_ordering

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.food_ordering.databinding.ActivityPayOutBinding
import com.example.food_ordering.fragment.CartFragment
import com.example.food_ordering.fragment.CongratsBottomFragment

class PayOutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPayOutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayOutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnPlaceMyOrder.setOnClickListener {
            val bottomSheetDialog = CongratsBottomFragment()
            bottomSheetDialog.show(supportFragmentManager, "Test")
        }
        binding.btnBackPayout.setOnClickListener {
            val intent = Intent(this, CartFragment::class.java)
            startActivity(intent)
        }
    }
}