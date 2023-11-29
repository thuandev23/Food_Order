package com.example.food_ordering

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.food_ordering.databinding.ActivityDetailsBinding

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding

    private var foodName:String ?= null
    private var foodPrice:String ?= null
    private var foodDescription:String ?= null
    private var foodImage:String ?= null
    private var foodIngredient:String ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBackDetails.setOnClickListener {
            finish()
        }

        foodName = intent.getStringExtra("menuItemName")
        foodPrice = intent.getStringExtra("menuItemPrice")
        foodDescription = intent.getStringExtra("menuItemDescription")
        foodImage = intent.getStringExtra("menuItemImage")
        foodIngredient = intent.getStringExtra("menuItemIngredient")

        with(binding){
            foodNameDetails.text = foodName
            foodDescriptionDetails.text = foodDescription
            foodIngredientDetails.text = foodIngredient
            Glide.with(this@DetailsActivity).load(Uri.parse(foodImage)).into(foodImageDetails)
        }
    }
}