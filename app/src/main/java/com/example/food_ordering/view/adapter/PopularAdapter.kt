package com.example.food_ordering.view.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.food_ordering.R
import com.example.food_ordering.activity.DetailsActivity
import com.example.food_ordering.databinding.PopularItemBinding
import com.example.food_ordering.model.CartItem

class PopularAdapter(
    private val popularItems: List<CartItem>,
    private val requireContext: Context
) : RecyclerView.Adapter<PopularAdapter.PopularViewHolder>() {

    inner class PopularViewHolder(private val binding: PopularItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    openDetailsActivity(position)
                }
            }
        }

        private fun openDetailsActivity(position: Int) {
            val menuItem = popularItems[position]
            val intent = Intent(requireContext, DetailsActivity::class.java).apply {
                putExtra("menuItemName", menuItem.foodName)
                putExtra("menuItemPrice", menuItem.foodPrice)
                putExtra("menuItemDescription", menuItem.foodDescription)
                putExtra("menuItemImage", menuItem.foodImage)
                putExtra("menuItemIngredient", menuItem.foodIngredient)
            }
            requireContext.startActivity(intent)
        }
        fun bind(position: Int) {
            val menuItem = popularItems[position]
            binding.apply {
                foodNamePopular.text = menuItem.foodName
                foodPricePopular.text = menuItem.foodPrice
                val uri = Uri.parse(menuItem.foodImage)
                Glide.with(requireContext).load(uri).into(imgFoodPopular)
                imgFoodPopular.clipToOutline = true
                isSold.text = buildString {
                    append(requireContext.getString(R.string.sold) + " ")
                    append(menuItem.foodQuantities.toString())
                }
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHolder {
        return PopularViewHolder(
            PopularItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return popularItems.size
    }

    override fun onBindViewHolder(holder: PopularViewHolder, position: Int) {
        holder.bind(position)
    }
}