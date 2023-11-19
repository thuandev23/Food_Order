package com.example.food_ordering.adapter

import android.content.Context
import android.content.Intent
import android.media.Image
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.food_ordering.DetailsActivity
import com.example.food_ordering.databinding.PopularItemBinding

class PopularAdapter(private val items:List<String>, private val price:List<String>, private val images: List<Int>, private val requireContext: Context) : RecyclerView.Adapter<PopularAdapter.PopularViewHolder>() {

    class PopularViewHolder(private val binding: PopularItemBinding) : RecyclerView.ViewHolder(binding.root){
        private val imagesView = binding.imgFoodPopular
        fun bind(item: String, price: String, image: Int) {
            binding.foodNamePopular.text = item
            binding.foodPricePopular.text = price
            imagesView.setImageResource(image)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHolder {
        return PopularViewHolder(PopularItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
       return items.size
    }

    override fun onBindViewHolder(holder: PopularViewHolder, position: Int) {
        val item = items[position]
        val price = price[position]
        val image =images[position]
        holder.bind(item, price, image)

        holder.itemView.setOnClickListener {
            // selection item-details
            val intent = Intent(requireContext, DetailsActivity::class.java)
            intent.putExtra("MenuItemName", item)
            intent.putExtra("MenuItemImage",image)
            requireContext.startActivity(intent)
        }
    }
}