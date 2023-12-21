package com.example.food_ordering

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import com.example.food_ordering.databinding.ActivityPayOutBinding
import com.example.food_ordering.fragment.CongratsBottomFragment
import com.example.food_ordering.model.AllVoucher
import com.example.food_ordering.model.OrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firestore.v1.StructuredQuery.Order

class PayOutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPayOutBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var name: String
    private lateinit var address: String
    private lateinit var phone: String
    private lateinit var totalAmount: String
    private lateinit var foodItemsName: ArrayList<String>
    private lateinit var foodItemPrices: ArrayList<String>
    private lateinit var foodImage: ArrayList<String>
    private lateinit var foodDescription: ArrayList<String>
    private lateinit var foodIngredient: ArrayList<String>
    private lateinit var foodQuantities: ArrayList<Int>
    private lateinit var userId: String
    private lateinit var totalAmountVoucher : String
    private lateinit var codeVoucher : String
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

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
        foodQuantities = intent.getIntegerArrayListExtra("foodQuantities") as ArrayList<Int>

        totalAmount = calculateTotalAmount().toString() + "$"
        binding.totalAmount.isEnabled = false
        binding.totalAmount.setText(totalAmount)

        totalAmountVoucher = calculateTotalAmount().toString() + "$"
        binding.totalAmountVoucher.isEnabled = false
        binding.totalAmountVoucher.setText(totalAmountVoucher)

        binding.btnApplyVoucher.setOnClickListener {
            codeVoucher = binding.codeVoucher.text.toString().trim()
            checkCodeVoucher(codeVoucher) { result ->
                totalAmountVoucher = "$result$"
                binding.totalAmountVoucher.isEnabled = false
                binding.totalAmountVoucher.setText(totalAmountVoucher)
            }
        }

        binding.btnPlaceMyOrder.setOnClickListener {
            name = binding.name.text.toString().trim()
            address = binding.address.text.toString().trim()
            phone = binding.phone.text.toString().trim()
            if (name.isBlank() && address.isBlank() && phone.isBlank()) {
                Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show()
            } else {
                placeOrder()
            }
        }
        binding.btnBackPayout.setOnClickListener {
            finish()
        }
    }

    private fun checkCodeVoucher(codeVoucher: String, callback: (Int) -> Unit) {
        database = FirebaseDatabase.getInstance()
        val voucherRef: DatabaseReference = database.reference.child("voucher")
        var totalAmountApplyVoucher = 0

        voucherRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var foundVoucher = false

                for (voucherSnapshot in dataSnapshot.children) {
                    val voucher = voucherSnapshot.getValue(AllVoucher::class.java)

                    if (voucher?.code == codeVoucher) {
                        val totalAmountInt = calculateTotalAmount()
                        val minPurchaseAmountInt = voucher.minPurchaseAmount?.toIntOrNull()
                        val discountAmountInt = voucher.discountAmount?.toIntOrNull()
                        val maxDiscountInt = voucher.maxDiscount?.toIntOrNull()
                        if (minPurchaseAmountInt != null && discountAmountInt != null && maxDiscountInt != null) {
                            if (totalAmountInt <= minPurchaseAmountInt) {
                                totalAmountApplyVoucher = totalAmountInt - discountAmountInt
                            } else if (totalAmountInt > minPurchaseAmountInt) {
                                totalAmountApplyVoucher = totalAmountInt - maxDiscountInt
                            }
                        }
                        foundVoucher = true
                        break
                    }
                }
                if (!foundVoucher) {
                    Toast.makeText(this@PayOutActivity, "Không tìm thấy mã voucher với code: $codeVoucher", Toast.LENGTH_SHORT).show()
                }
                // Pass the result to the callback
                callback(totalAmountApplyVoucher)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@PayOutActivity, "Đã xảy ra lỗi khi đọc dữ liệu từ Firebase: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun placeOrder() {
        userId = auth.currentUser?.uid ?: ""
        val time = System.currentTimeMillis()
        val itemPushKey = databaseReference.child("OrderDetails").push().key
        totalAmount = totalAmountVoucher
        val orderDetails = OrderDetails(
            userId,
            name,
            foodItemsName,
            foodItemPrices,
            foodImage,
            foodQuantities,
            address,
            totalAmount,
            phone,
            time,
            itemPushKey,
            false,
            false
        )
        val orderReference = databaseReference.child("OrderDetails").child(itemPushKey!!)
        orderReference.setValue(orderDetails).addOnSuccessListener {
            val bottomSheetDialog = CongratsBottomFragment()
            bottomSheetDialog.show(supportFragmentManager, "Test")
            adOrderToHistory(orderDetails)
            removeItemFromCart()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to order", Toast.LENGTH_SHORT).show()
        }
    }
    private fun adOrderToHistory(orderDetails: OrderDetails) {
        databaseReference.child("user").child(userId).child("BuyHistory")
            .child(orderDetails.itemPushKey!!)
            .setValue(orderDetails).addOnSuccessListener {

            }.addOnFailureListener {

            }
    }
    private fun removeItemFromCart() {
        val cartItemsReference = databaseReference.child("user").child(userId).child("CartItems")
        cartItemsReference.removeValue()
    }
    private fun calculateTotalAmount(): Int {
        var totalAmount = 0
        for (i in 0 until foodItemPrices.size) {
            var price = foodItemPrices[i]
            val lastChar = price.last()
            val priceIntVale = if (lastChar == '$') {
                price.dropLast(1).replace(",", "").toInt()
            } else {
                price.replace(",", "").toInt()
            }
            var quantity = foodQuantities[i]
            totalAmount += priceIntVale * quantity
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