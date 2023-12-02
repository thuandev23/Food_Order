package com.example.food_ordering

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.food_ordering.adapter.CartAdapter
import com.example.food_ordering.databinding.ActivityPayOutBinding
import com.example.food_ordering.databinding.FragmentCartBinding
import com.example.food_ordering.fragment.CartFragment
import com.example.food_ordering.fragment.CongratsBottomFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.net.Inet4Address

class PayOutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPayOutBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var totalAmount: String
    private lateinit var foodItemsName: ArrayList<String>
    private lateinit var foodItemPrices: ArrayList<String>
    private lateinit var foodImage: ArrayList<String>
    private lateinit var foodDescription: ArrayList<String>
    private lateinit var foodIngredient: ArrayList<String>
    private lateinit var foodQuantity: ArrayList<Int>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // initialize Firebase and User details
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference()
        // set User Data
        setUserData()

        //get user details form firebase

        val intent = intent
        foodItemsName = intent.getStringArrayListExtra("foodItemName") as ArrayList<String>
        foodItemPrices = intent.getStringArrayListExtra("foodPrice") as ArrayList<String>
        foodImage = intent.getStringArrayListExtra("foodImage") as ArrayList<String>
        foodDescription = intent.getStringArrayListExtra("foodDescription") as ArrayList<String>
        foodIngredient = intent.getStringArrayListExtra("foodIngredient") as ArrayList<String>
        foodQuantity = intent.getIntegerArrayListExtra("foodQuantities") as ArrayList<Int>

        totalAmount = calculateTotalAmount().toString() + "$"
        binding.totalAmount.isEnabled = false
        binding.totalAmount.setText(totalAmount)

        binding.btnPlaceMyOrder.setOnClickListener {
            val bottomSheetDialog = CongratsBottomFragment()
            bottomSheetDialog.show(supportFragmentManager, "Test")
        }
        binding.btnBackPayout.setOnClickListener {
            finish()
        }
    }
        // khi nhấn process thì bị văng ra -> lỗi chỗ quantity
    private fun calculateTotalAmount(): Int {
        var totalAmount = 0
        if (foodItemPrices.isNotEmpty()) {
            for (i in 0 until foodItemPrices.size) {
                var price = foodItemPrices[i]
                val lastChar = price.last()
                val priceIntVale = if (lastChar == '$') {
                    price.dropLast(1).replace(",", "").toInt()
                } else {
                    price.replace(",", "").toInt()
                }
                // khi comment lại thì không bị lỗi nhưng không thể nhân với số lượng khi tổng tiền
                //var quantity = foodQuantity[i]
                totalAmount += priceIntVale
            }
        }
        return totalAmount
    }

    private fun setUserData() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val usersReference = databaseReference.child("user").child(userId)
            usersReference.addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val names = snapshot.child("name").getValue(String::class.java) ?: ""
                        val addresss = snapshot.child("address").getValue(String::class.java) ?: ""
                        val phones = snapshot.child("phone").getValue(String::class.java) ?: ""
                        binding.apply {
                            name.setText(names)
                            address.setText(addresss)
                            phone.setText(phones)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("DatabaseUserError", "Error: ${error.message}")
                }

            })
        }
    }
}