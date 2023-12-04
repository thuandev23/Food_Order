package com.example.food_ordering.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.food_ordering.databinding.RecentBuyItemsBinding

class RecentBuyAdapter (
    private var context: Context,
    private var foodNameList: ArrayList<String>,
    private var foodImageList: ArrayList<String>,
    private var foodPriceList: ArrayList<String>,
//    private var foodQuantityList: ArrayList<Int>,
) : RecyclerView.Adapter<RecentBuyAdapter.RecentBuyViewHolder>(){
    inner class RecentBuyViewHolder(private val binding: RecentBuyItemsBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
                binding.apply {
                    foodNameRecentBuy.text = foodNameList[position]
                    foodPriceRecentBuy.text = foodPriceList[position]
//                    foodQuantityRecentBuy.text = foodQuantityList[position].toString()
                    val uri = Uri.parse(foodImageList[position])
                    Glide.with(context).load(uri).into(imgFoodRecentBuy)
                }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentBuyViewHolder {
        val binding = RecentBuyItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecentBuyViewHolder(binding)
    }

    override fun getItemCount(): Int = foodNameList.size

    override fun onBindViewHolder(holder: RecentBuyViewHolder, position: Int) {
        holder.bind(position)
    }
}