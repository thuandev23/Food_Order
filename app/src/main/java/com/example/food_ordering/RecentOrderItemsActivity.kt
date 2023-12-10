package com.example.food_ordering

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.food_ordering.adapter.RecentBuyAdapter
import com.example.food_ordering.databinding.ActivityRecentOrderItemsBinding
import com.example.food_ordering.model.OrderDetails

class RecentOrderItemsActivity : AppCompatActivity() {
    private val binding: ActivityRecentOrderItemsBinding by lazy {
        ActivityRecentOrderItemsBinding.inflate(layoutInflater)
    }
    private lateinit var allFoodNameList: ArrayList<String>
    private lateinit var allFoodImageList: ArrayList<String>
    private lateinit var allFoodPriceList: ArrayList<String>
    private lateinit var allFoodQuantityList: ArrayList<Int>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnBackPayout.setOnClickListener { finish() }

        val recentOrderItems =
            intent.getSerializableExtra("RecentBuyOrderItem") as? ArrayList<OrderDetails>
        recentOrderItems?.let { orderDetails ->
            if (orderDetails.isNotEmpty()) {
                val recentOrderItem = orderDetails[0]
                allFoodNameList = recentOrderItem.foodNames as ArrayList<String>
                allFoodImageList = recentOrderItem.foodImages as ArrayList<String>
                allFoodPriceList = recentOrderItem.foodPrices as ArrayList<String>
                allFoodQuantityList = recentOrderItem.foodQuantities as ArrayList<Int>
            }

        }
        setAdapter()
    }

    private fun setAdapter() {
        val rv = binding.recentBuyRecyclerView
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = RecentBuyAdapter(this, allFoodNameList, allFoodImageList, allFoodPriceList,allFoodQuantityList)

    }
}