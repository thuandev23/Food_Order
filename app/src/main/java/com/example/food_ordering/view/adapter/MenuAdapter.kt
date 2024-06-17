package com.example.food_ordering.view.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.food_ordering.activity.DetailsActivity
import com.example.food_ordering.databinding.MenuItemBinding
import com.example.food_ordering.model.AllItemMenu

class MenuAdapter(
    private var menuItems: List<AllItemMenu>,
    private val requireContext: Context
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = MenuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun getItemCount(): Int = menuItems.size

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(position)
    }

    fun updateMenuItems(newMenuItems: List<AllItemMenu>) {
        menuItems = newMenuItems
        notifyDataSetChanged()
    }

    inner class MenuViewHolder(private val binding: MenuItemBinding) :
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
            val menuItem = menuItems[position]
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
            val menuItem = menuItems[position]
            binding.apply {
                foodNamePopularMenu.text = menuItem.foodName
                foodPricePopularMenu.text = menuItem.foodPrice
                val uri = Uri.parse(menuItem.foodImage)
                Glide.with(requireContext).load(uri).into(imgFoodPopularMenu)
            }
        }

    }
}

