package com.example.food_ordering.view.fragment

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.food_ordering.R
import com.example.food_ordering.view.activity.RecentOrderItemsActivity
import com.example.food_ordering.view.adapter.BuyAgainAdapter
import com.example.food_ordering.databinding.BuyAgainItemBinding
import com.example.food_ordering.databinding.FragmentHistoryBinding
import com.example.food_ordering.model.OrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var buyAgainAdapter: BuyAgainAdapter
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var userId:String
    private var listOfOrderItem: MutableList<OrderDetails> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHistoryBinding.inflate(layoutInflater, container, false)
        // initialize Firebase (auth, database)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        binding.recentBuyItem.setOnClickListener {
            seeItemsRecentBuy()
        }
        retrieveBuyHistory()
        binding.btnReceived.setOnClickListener {
            updateOrderDetailsToPayStatus()
        }

        return binding.root
    }

    private fun updateOrderDetailsToPayStatus() {
        val itemPushKey = listOfOrderItem[0].itemPushKey
        val completeOrderReference = database.reference.child("CompletedOrder").child(itemPushKey!!)
        completeOrderReference.child("paymentReceived").setValue(true)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Received", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Not Receive", Toast.LENGTH_SHORT).show()
            }
    }

    private fun seeItemsRecentBuy() {
        listOfOrderItem.firstOrNull()?.let { recentBuy->
            val intent = Intent(requireContext(), RecentOrderItemsActivity::class.java)
            intent.putExtra("RecentBuyOrderItem", ArrayList(listOfOrderItem))
            startActivity(intent)
        }
    }

    private fun retrieveBuyHistory() {
        userId = auth.currentUser?.uid?:""
        val buyItemReference:DatabaseReference = database.reference.child("accounts").child("users").child(userId).child("BuyHistory")
        val shortingQuery = buyItemReference.orderByChild("currentTime")
        shortingQuery.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (buySnapshot in snapshot.children){
                    val buyHistory = buySnapshot.getValue(OrderDetails::class.java)
                    buyHistory?.let {
                        listOfOrderItem.add(it)
                    }
                }
                listOfOrderItem.reverse()
                if(listOfOrderItem.isNotEmpty()){
                    setDataInRecentBuyItem()
                    setPreviousBuyItemsRecyclerView()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun setDataInRecentBuyItem() {
        binding.recentBuyItem.visibility = View.VISIBLE
        val recentOrderItem = listOfOrderItem.firstOrNull()
        recentOrderItem?.let {
            with(binding) {
                val foodNames = it.foodNames?.joinToString(", ") ?: ""
                buyAgainFoodName.text = foodNames

                val totalPrice = it.foodPrices?.sumOf { price -> price.toDoubleOrNull() ?: 0.0 } ?: 0.0
                buyAgainFoodPrice.text = getString(R.string.total_price_vnd ) + totalPrice

                buyAgainFoodImage.visibility = it.foodImages?.let { images ->
                    if (images.isNotEmpty()) {
                        val uri = Uri.parse(images[0])
                        Glide.with(requireContext()).load(uri).into(buyAgainFoodImage)
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                } ?: View.GONE

                val isOrdersAccepted = it.orderAccepted
                if (isOrdersAccepted) {
                    orderStatus.background.setTint(Color.GREEN)
                    btnReceived.visibility = View.VISIBLE
                } else {
                    orderStatus.background.setTint(Color.RED)
                    btnReceived.visibility = View.GONE
                }
            }
        }
    }


    private fun setPreviousBuyItemsRecyclerView() {
        val buyAgainFoodName = mutableListOf<String>()
        val buyAgainFoodPrice = mutableListOf<String>()
        val buyAgainFoodImage = mutableListOf<String>()
        for (order in listOfOrderItem) {
            order.foodNames?.let { names ->
                order.foodPrices?.let { prices ->
                    order.foodImages?.let { images ->
                        for (i in names.indices) {
                            buyAgainFoodName.add(names[i])
                            buyAgainFoodPrice.add(prices[i])
                            buyAgainFoodImage.add(images[i])
                        }
                    }
                }
            }
        }
        val rv = binding.buyAgainRecyclerView
        rv.layoutManager = LinearLayoutManager(requireContext())
        buyAgainAdapter = BuyAgainAdapter(
            buyAgainFoodName,
            buyAgainFoodPrice,
            buyAgainFoodImage,
            requireContext()
        )
        rv.adapter = buyAgainAdapter
    }
}

