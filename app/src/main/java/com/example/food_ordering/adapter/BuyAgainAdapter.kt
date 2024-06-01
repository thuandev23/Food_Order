package com.example.food_ordering.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.food_ordering.activity.DetailsActivity
import com.example.food_ordering.activity.EvaluateActivity
import com.example.food_ordering.databinding.BuyAgainItemBinding

class BuyAgainAdapter(
    private val buyAgainFoodName: MutableList<String>,
    private val buyAgainFoodPrice: MutableList<String>,
    private val buyAgainFoodImage: MutableList<String>,
    private val requireContext: Context
) : RecyclerView.Adapter<BuyAgainAdapter.BuyAgainViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuyAgainViewHolder {
        val binding =
            BuyAgainItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BuyAgainViewHolder(binding)
    }

    override fun getItemCount(): Int = buyAgainFoodImage.size

    override fun onBindViewHolder(holder: BuyAgainViewHolder, position: Int) {
        holder.bind(
            buyAgainFoodName[position],
            buyAgainFoodPrice[position],
            buyAgainFoodImage[position],
        )
        holder.binding.btnBuyAgain.setOnClickListener {
            val intent = Intent(requireContext, DetailsActivity::class.java).apply {
                putExtra("menuItemName", buyAgainFoodName[position])
                putExtra("menuItemPrice", buyAgainFoodPrice[position])
                putExtra("menuItemImage", buyAgainFoodImage[position])
            }
            requireContext.startActivity(intent)
        }
        holder.binding.btnEvaluate.setOnClickListener {
            val intent = Intent(requireContext, EvaluateActivity::class.java).apply {
                putExtra("menuItemName", buyAgainFoodName[position])
            }
            requireContext.startActivity(intent)
        }
    }
    inner class BuyAgainViewHolder(val binding: BuyAgainItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(foodName: String, foodPrice: String, foodImage: String) {
            binding.buyAgainFoodName.text = foodName
            binding.buyAgainFoodPrice.text = foodPrice
            val uri = Uri.parse(foodImage)
            Glide.with(requireContext).load(uri).into(binding.buyAgainFoodImage)
        }

    }

}