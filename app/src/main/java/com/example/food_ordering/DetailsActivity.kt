package com.example.food_ordering

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.food_ordering.databinding.ActivityDetailsBinding

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.btnBackDetails.setOnClickListener {
            finish()
        }

        val foodName = intent.getStringExtra("MenuItemName")
        val foodImage = intent.getIntExtra("MenuItemImage", 0)
        binding.foodNameDetails.text = foodName
        binding.foodImageDetails.setImageResource(foodImage)
    }
}