package com.example.food_ordering.activity

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.food_ordering.adapter.SelectVoucherAdapter
import com.example.food_ordering.databinding.ActivityPayOutBinding
import com.example.food_ordering.databinding.DialogVoucherListBinding
import com.example.food_ordering.fragment.CongratsBottomFragment
import com.example.food_ordering.model.AllVoucher
import com.example.food_ordering.model.OrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class PayOutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPayOutBinding

    private lateinit var foodItemsName: ArrayList<String>
    private lateinit var foodItemPrices: ArrayList<String>
    private lateinit var foodImage: ArrayList<String>
    private lateinit var foodDescription: ArrayList<String>
    private lateinit var foodIngredient: ArrayList<String>
    private lateinit var foodQuantities: ArrayList<Int>
    private lateinit var totalAmountVoucher: String
    private lateinit var codeVoucher: String

    private lateinit var name: String
    private lateinit var address: String
    private lateinit var phone: String
    private lateinit var totalAmount: String

    private lateinit var userId: String
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    private val validVoucherSelectedItems = mutableListOf<AllVoucher>()
    private val selectedVouchers = mutableListOf<AllVoucher>()
    private var previousSelectedVoucher = mutableListOf<AllVoucher>()
    private var countVoucherSelected :Int = 0

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

        binding.btnSelectVoucher.setOnClickListener {
            showVoucherSelectionDialog()
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

        /*binding.paymentButtonContainer.setup(
            createOrder =
            CreateOrder { createOrderActions ->
                val order =
                    OrderRequest(
                        intent = OrderIntent.CAPTURE,
                        appContext = AppContext(userAction = UserAction.PAY_NOW),
                        purchaseUnitList =
                        listOf(
                            PurchaseUnit(
                                amount =
                                Amount(currencyCode = CurrencyCode.USD, value = x)
                            )
                        )
                    )
                createOrderActions.create(order)
            },
            onApprove =
            OnApprove { approval ->
                approval.orderActions.capture { captureOrderResult ->
                    Log.i("CaptureOrder", "CaptureOrderResult: $captureOrderResult")
                }
            },
            onCancel = OnCancel {
                Log.d("OnCancel", "Buyer canceled the PayPal experience.")
            },
            onError = OnError { errorInfo ->
                Log.d("OnError", "Error: $errorInfo")
            }
        )*/
    }

    private fun showVoucherSelectionDialog() {
        userId = auth.currentUser?.uid ?: ""
        database = FirebaseDatabase.getInstance()
        val voucherRef: DatabaseReference =
            database.reference.child("accounts").child("users").child(userId).child("MyVouchers")
        voucherRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                validVoucherSelectedItems.clear()
                for (voucherSnapshot in dataSnapshot.children) {
                    val voucherSelected = voucherSnapshot.getValue(AllVoucher::class.java)
                    voucherSelected?.let {
                        if (!it.expiryDate?.let { it1 -> isVoucherExpired(it1) }!!) {
                            validVoucherSelectedItems.add(it)
                        }
                    }
                }
                voucherSelectedAdapter(validVoucherSelectedItems)
            }

            private fun isVoucherExpired(expiryDate: String): Boolean {
                val currentDateString = LocalDate.now().format(DateTimeFormatter.ISO_DATE)
                return expiryDate > currentDateString
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(
                    this@PayOutActivity,
                    "Error fetching vouchers: ${databaseError.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun voucherSelectedAdapter(items: List<AllVoucher>) {
        val dialogBinding = DialogVoucherListBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(this).setView(dialogBinding.root).create()
        val selectedVouchers = previousSelectedVoucher.toMutableSet()
        countVoucherSelected = selectedVouchers.size
        binding.countVoucher.setText("$countVoucherSelected voucher selected")
        if (items.isEmpty()) {
            dialogBinding.voucherRecyclerView.visibility = android.view.View.GONE
            dialogBinding.noVoucher.visibility = android.view.View.VISIBLE
            dialogBinding.btnApplyVoucher.setOnClickListener {
                dialog.dismiss()
            }
        }else{
            dialogBinding.voucherRecyclerView.visibility = android.view.View.VISIBLE
            dialogBinding.noVoucher.visibility = android.view.View.GONE
            dialogBinding.voucherRecyclerView.layoutManager = LinearLayoutManager(this)
            dialogBinding.voucherRecyclerView.adapter = SelectVoucherAdapter(items, selectedVouchers) { voucher, isSelected ->
                if (isSelected) {
                    countVoucherSelected += 1
                    selectedVouchers.add(voucher)
                } else {
                    countVoucherSelected -= 1
                    selectedVouchers.remove(voucher)
                }
                binding.countVoucher.setText("$countVoucherSelected voucher selected")
            }

            dialogBinding.btnApplyVoucher.setOnClickListener {
                applySelectedVouchers(selectedVouchers)
                previousSelectedVoucher = selectedVouchers.toMutableList()
                dialog.dismiss()
            }
        }
        dialog.setOnShowListener {
            dialog.window?.setLayout(
                (resources.displayMetrics.widthPixels * 1.01).toInt(),
                (resources.displayMetrics.heightPixels * 0.75).toInt()
            )
        }
        dialog.show()
    }

    private fun applySelectedVouchers(selectedVouchers: MutableSet<AllVoucher>) {
        var totalAmount = calculateTotalAmount()
        val validVouchers = mutableListOf<AllVoucher>()
        validVouchers.addAll(selectedVouchers)
        validVouchers.forEach { voucher ->
            val minPurchaseAmountInt = voucher?.minPurchaseAmount?.toIntOrNull()
            val discountAmountInt = voucher?.discountAmount?.toIntOrNull()
            val maxDiscountInt = voucher?.maxDiscount?.toIntOrNull()
            var discount =  0;
            if (minPurchaseAmountInt != null && discountAmountInt != null && maxDiscountInt != null) {
                if (totalAmount <= minPurchaseAmountInt) {
                    discount = discountAmountInt
                } else if (totalAmount > minPurchaseAmountInt) {
                    discount = maxDiscountInt
                }
            }
            totalAmount -= discount;
            // Đánh dấu voucher là đã sử dụng
            voucher.isUsed = true
            markVoucherAsUsed(voucher)
        }
        totalAmountVoucher = "${totalAmount}$"
        binding.totalAmountVoucher.setText(totalAmountVoucher)
    }

    private fun markVoucherAsUsed(voucher: AllVoucher) {
        userId = auth.currentUser?.uid ?: ""
        val isUsedVoucherRef = database.reference.child("accounts").child("users").child(userId).child("MyVouchers").child(voucher.id!!)
        isUsedVoucherRef.child("isUsed").setValue(true).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("markVoucherAsUsed", "Voucher ${voucher.code} marked as used")
            } else {
                Log.e("markVoucherAsUsed", "Failed to mark voucher ${voucher.code} as used")
            }
        }
    }

    private fun placeOrder() {
        userId = auth.currentUser?.uid ?: ""
        val time = System.currentTimeMillis()
        val itemPushKey = databaseReference.child("OrderDetails").push().key
        totalAmount = totalAmountVoucher.toString()
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
        databaseReference.child("accounts").child("users").child(userId).child("BuyHistory")
            .child(orderDetails.itemPushKey!!).setValue(orderDetails).addOnSuccessListener {

            }.addOnFailureListener {

            }
    }

    private fun removeItemFromCart() {
        val cartItemsReference =
            databaseReference.child("accounts").child("users").child(userId).child("CartItems")
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
            val usersReference = databaseReference.child("accounts").child("users").child(userId)
            usersReference.addListenerForSingleValueEvent(object : ValueEventListener {
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