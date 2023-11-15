package com.example.food_ordering.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.food_ordering.databinding.MenuItemBinding

class MenuAdapter(private val menuItemsName: MutableList<String>, private val menuItemsPrice: List<String>, private val menuItemsImage: List<Int>):RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = MenuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun getItemCount(): Int = menuItemsName.size

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(position)
    }
    inner class MenuViewHolder(private val binding: MenuItemBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
             binding.apply {
                foodNamePopularMenu.text= menuItemsName[position]
                 foodPricePopularMenu.text = menuItemsPrice[position]
                 imgFoodPopularMenu.setImageResource(menuItemsImage[position])
             }
        }

    }
}