package com.example.food_ordering.view.activity

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.food_ordering.R
import com.example.food_ordering.view.adapter.SelectVoucherAdapter
import com.example.food_ordering.databinding.ActivityPayOutBinding
import com.example.food_ordering.databinding.DialogVoucherListBinding
import com.example.food_ordering.model.AllVoucher
import com.example.food_ordering.model.CartItem
import com.example.food_ordering.model.OrderDetails
import com.example.food_ordering.view.fragment.CongratsBottomFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.thuan.zalopay.Api.CreateOrder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject
import vn.zalopay.sdk.Environment
import vn.zalopay.sdk.ZaloPayError
import vn.zalopay.sdk.ZaloPaySDK
import vn.zalopay.sdk.listeners.PayOrderListener
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
    private lateinit var name: String
    private lateinit var address: String
    private lateinit var phone: String
    private lateinit var totalAmount: String

    private lateinit var userId: String
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    private var previousSelectedVoucher = mutableListOf<AllVoucher>()
    private var countVoucherSelected :Int = 0
    private var payToMerchant: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // initialize Firebase and User details
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference()
        //get user details form firebase
        initializeUserDetails()

        val intent = intent
        foodItemsName = intent.getStringArrayListExtra("foodItemName") as ArrayList<String>
        foodItemPrices = intent.getStringArrayListExtra("foodPrice") as ArrayList<String>
        foodImage = intent.getStringArrayListExtra("foodImage") as ArrayList<String>
        foodDescription = intent.getStringArrayListExtra("foodDescription") as ArrayList<String>
        foodIngredient = intent.getStringArrayListExtra("foodIngredient") as ArrayList<String>
        foodQuantities = intent.getIntegerArrayListExtra("foodQuantities") as ArrayList<Int>

        totalAmount = calculateTotalAmount().toString()
        binding.totalAmount.isEnabled = false
        binding.totalAmount.setText(totalAmount + " VND")

        totalAmountVoucher = calculateTotalAmount().toString()
        binding.totalAmountVoucher.isEnabled = false
        binding.totalAmountVoucher.setText(totalAmountVoucher + " VND")

        binding.btnSelectVoucher.setOnClickListener {
            showVoucherSelectionDialog()
        }

        binding.btnPlaceMyOrder.setOnClickListener {
            name = binding.name.text.toString().trim()
            address = binding.address.text.toString().trim()
            phone = binding.phone.text.toString().trim()
            if (name.isBlank() && address.isBlank() && phone.isBlank()) {
                Toast.makeText(this, getString(R.string.please_fill_all_details), Toast.LENGTH_SHORT).show()
            } else {
                placeOrder()
            }
        }
        binding.btnBackPayout.setOnClickListener {
            finish()
        }
        // ZaloPay
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        // ZaloPay SDK Init
        ZaloPaySDK.init(2553, Environment.SANDBOX)

        binding.textViewPaymentMethod.setOnClickListener {
            showPaymentMethodsDialog()
        }
    }

    private fun showPaymentMethodsDialog() {
        val paymentMethods = arrayOf("Trả sau khi nhận hàng", "ZaloPay")
        val paymentMethodImages = arrayOf(R.drawable.cashondelivery, R.drawable.zalopay)

        AlertDialog.Builder(this)
            .setTitle("Chọn phương thức thanh toán")
            .setItems(paymentMethods) { dialog, which ->
                val selectedMethod = paymentMethods[which]
                binding.imageViewMethodPay.setImageResource(paymentMethodImages[which])
                payToMerchant = if (selectedMethod == "ZaloPay") { true } else { false }
            }
            .create()
            .show()
    }

    private fun showVoucherSelectionDialog() {
        userId = auth.currentUser?.uid ?: ""
        database = FirebaseDatabase.getInstance()
        val voucherRef = database.reference.child("accounts").child("users").child(userId).child("MyVouchers")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dataSnapshot = voucherRef.get().await()
                val validVouchers = dataSnapshot.children.mapNotNull { it.getValue(AllVoucher::class.java) }
                    .filter { it.expiryDate?.let { expiryDate -> !isVoucherExpired(expiryDate) } == true }

                withContext(Dispatchers.Main) {
                    voucherSelectedAdapter(validVouchers)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PayOutActivity, "Error fetching vouchers: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun isVoucherExpired(expiryDate: String): Boolean {
        val currentDateString = LocalDate.now().format(DateTimeFormatter.ISO_DATE)
        return expiryDate <= currentDateString
    }

    private fun voucherSelectedAdapter(items: List<AllVoucher>) {
        val dialogBinding = DialogVoucherListBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(this).setView(dialogBinding.root).create()
        val selectedVouchers = previousSelectedVoucher.toMutableSet()
        countVoucherSelected = selectedVouchers.size
        binding.countVoucher.setText(countVoucherSelected.toString() + getString(R.string.voucher_selected))
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
                binding.countVoucher.setText(countVoucherSelected.toString() + getString(R.string.voucher_selected))
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
        totalAmountVoucher = totalAmount.toString()
        binding.totalAmountVoucher.setText(totalAmountVoucher + "VND")
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
            false,
            payToMerchant
        )

        // check payToMerchant = true -> save data to child("Transaction History")
        if (payToMerchant) {
            payToMerchant = true
            paytoZaloPay(totalAmount, orderDetails, itemPushKey)
        }else{
            saveDataPaymentDefault(orderDetails, itemPushKey)
        }
    }

    private fun paytoZaloPay(totalAmount: String, orderDetails: OrderDetails, itemPushKey: String?) {
        val orderApi = CreateOrder()
        try {
            val data: JSONObject? = orderApi.createOrder(totalAmount)
            Log.d("Amount", data.toString())
            val code = data?.getString("return_code")
            if(code.equals("1")){
                val token = data?.getString("zp_trans_token")
                ZaloPaySDK.getInstance().payOrder(this, token!!,"demozpdk://app", object :
                    PayOrderListener {
                    override fun onPaymentSucceeded(transactionId: String?, transToken: String?, appTransID: String?) {
                        runOnUiThread {
                            AlertDialog.Builder(this@PayOutActivity)
                                .setTitle("Payment Success")
                                .setMessage(
                                    java.lang.String.format(
                                        "TransactionId: %s - TransToken: %s",
                                        transactionId,
                                        transToken
                                    )
                                )
                                .setPositiveButton("OK") { dialog, which -> }
                                .setNegativeButton("Cancel", null).show()
                        }
                        saveDataPaymentZaloPay(orderDetails, itemPushKey)
                        saveDataPaymentDefault(orderDetails, itemPushKey)
                    }

                    override fun onPaymentCanceled(zpTransToken: String?, appTransID: String?) {
                         AlertDialog.Builder(this@PayOutActivity)
                             .setTitle("User Cancel Payment")
                             .setMessage(
                                 java.lang.String.format(
                                     "zpTransToken: %s \n",
                                     zpTransToken
                                 )
                             )
                             .setPositiveButton("OK") { dialog, which -> }
                             .setNegativeButton("Cancel", null).show()

                    }

                    override fun onPaymentError(zaloPayError: ZaloPayError?, zpTransToken: String?, appTransID: String?) {
                        AlertDialog.Builder(this@PayOutActivity)
                            .setTitle("Payment Fail")
                            .setMessage(
                                java.lang.String.format(
                                    "ZaloPayErrorCode: %s \nTransToken: %s",
                                    zaloPayError.toString(),
                                    zpTransToken
                                )
                            )
                            .setPositiveButton("OK") { dialog, which -> }
                            .setNegativeButton("Cancel", null).show()

                    }

                })
            }
            else{
                Toast.makeText(this, "Error: ${data?.getString("return_message")}", Toast.LENGTH_SHORT).show()
            }
        }catch (e: Exception){
            e.printStackTrace()
        }

    }

    private fun saveDataPaymentZaloPay(orderDetails: OrderDetails, itemPushKey: String?) {
        val orderReference = databaseReference.child("accounts").child("users")
            .child(userId).child("TransactionHistory").child(itemPushKey!!)
        orderReference.setValue(orderDetails).addOnSuccessListener {
            val bottomSheetDialog = CongratsBottomFragment()
            bottomSheetDialog.show(supportFragmentManager, "Test")
            adOrderToHistory(orderDetails)
            removeItemFromCart()
            addQuantityToPopularItems()
        }.addOnFailureListener {
            Toast.makeText(this@PayOutActivity, "Failed to order", Toast.LENGTH_SHORT).show()
        }
    }
    private fun saveDataPaymentDefault(orderDetails: OrderDetails, itemPushKey: String?) {
        val orderReference = databaseReference.child("OrderDetails").child(itemPushKey!!)
        orderReference.setValue(orderDetails).addOnSuccessListener {
            val bottomSheetDialog = CongratsBottomFragment()
            bottomSheetDialog.show(supportFragmentManager, "Test")
            adOrderToHistory(orderDetails)
            removeItemFromCart()
            addQuantityToPopularItems()
        }.addOnFailureListener {
            Toast.makeText(this@PayOutActivity, "Failed to order", Toast.LENGTH_SHORT).show()
        }
    }
    private fun addQuantityToPopularItems() {
        for (i in 0 until foodItemsName.size) {
            val foodName = foodItemsName[i]
            val foodImage = foodImage[i]
            val foodPrice = foodItemPrices[i]
            val foodDescription = foodDescription[i]
            val foodIngredient = foodIngredient[i]
            val foodQuantity = foodQuantities[i]
            val itemsSold = CartItem(foodName, foodPrice, foodDescription, foodImage, foodIngredient, foodQuantity)
            val popularItemsReference = databaseReference.child("ProductsSold").child(foodName)

            popularItemsReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val currentQuantity = snapshot.child("foodQuantities").getValue(Int::class.java) ?: 0
                        popularItemsReference.child("foodQuantities").setValue(currentQuantity + foodQuantity)
                    } else {
                        popularItemsReference.setValue(itemsSold)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Error fetching popular items: ${error.message}")
                }
            })
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

    private fun initializeUserDetails() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            userId = currentUser.uid
            val usersReference = databaseReference.child("accounts").child("users").child(userId)

            usersReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        binding.apply {
                            name.setText(snapshot.child("name").getValue(String::class.java) ?: "")
                            address.setText(snapshot.child("address").getValue(String::class.java) ?: "")
                            phone.setText(snapshot.child("phone").getValue(String::class.java) ?: "")
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Error fetching user data: ${error.message}")
                }
            })
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        ZaloPaySDK.getInstance().onResult(intent)
    }
}