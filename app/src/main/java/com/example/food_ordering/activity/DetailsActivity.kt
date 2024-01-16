package com.example.food_ordering.activity

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.food_ordering.databinding.ActivityDetailsBinding
import com.example.food_ordering.model.CartItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding

    private var foodName: String? = null
    private var foodPrice: String? = null
    private var foodDescription: String? = null
    private var foodImage: String? = null
    private var foodIngredient: String? = null
    private var foodQuantity:String ? = null
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()
        binding.btnBackDetails.setOnClickListener {
            finish()
        }

        foodName = intent.getStringExtra("menuItemName")
        foodPrice = intent.getStringExtra("menuItemPrice")
        foodDescription = intent.getStringExtra("menuItemDescription")
        foodImage = intent.getStringExtra("menuItemImage")
        foodIngredient = intent.getStringExtra("menuItemIngredient")

        with(binding) {
            foodNameDetails.text = foodName
            foodDescriptionDetails.text = foodDescription
            foodIngredientDetails.text = foodIngredient
            Glide.with(this@DetailsActivity).load(Uri.parse(foodImage)).into(foodImageDetails)
        }

        binding.btnAddToCart.setOnClickListener {
            addItemToCart()
        }
    }

    private fun addItemToCart() {
        val database = FirebaseDatabase.getInstance().reference
        val userId = auth.currentUser?.uid ?: ""

        val cartItem = CartItem(
            foodName.toString(),
            foodPrice.toString(),
            foodDescription.toString(),
            foodImage.toString(),
            foodIngredient.toString(),
            1
        )
        database.child("accounts").child("users").child(userId).child("CartItems").push().setValue(cartItem)
            .addOnSuccessListener {
                Toast.makeText(this, "Item added into cart successfully", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener {
            Toast.makeText(this, "Item added into cart failed", Toast.LENGTH_SHORT).show()

        }
    }
}