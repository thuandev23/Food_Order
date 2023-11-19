package com.example.food_ordering.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.food_ordering.DetailsActivity
import com.example.food_ordering.databinding.MenuItemBinding

class MenuAdapter(
    private val menuItemsName: MutableList<String>,
    private val menuItemsPrice: List<String>,
    private val menuItemsImage: List<Int>,
    private val requireContext: Context
):RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    private val itemClickListener: OnClickListener ?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = MenuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun getItemCount(): Int = menuItemsName.size

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(position)
    }
    inner class MenuViewHolder(private val binding: MenuItemBinding):RecyclerView.ViewHolder(binding.root) {
       init {
           binding.root.setOnClickListener {
               val position = adapterPosition
               if (position!= RecyclerView.NO_POSITION){
                   itemClickListener?.onItemClick(position)
               }

               // selection item-details
               val intent = Intent(requireContext, DetailsActivity::class.java)
               intent.putExtra("MenuItemName", menuItemsName.get(position))
               intent.putExtra("MenuItemImage",menuItemsImage.get(position))
                requireContext.startActivity(intent)
           }
       }
        fun bind(position: Int) {
             binding.apply {
                foodNamePopularMenu.text= menuItemsName[position]
                 foodPricePopularMenu.text = menuItemsPrice[position]
                 imgFoodPopularMenu.setImageResource(menuItemsImage[position])

             }
        }

    }
    interface OnClickListener{
        fun onItemClick(position: Int) {

        }
    }
}

