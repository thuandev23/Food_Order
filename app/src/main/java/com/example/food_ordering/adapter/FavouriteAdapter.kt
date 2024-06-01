package com.example.food_ordering.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.food_ordering.databinding.FavouriteItemBinding
import com.example.food_ordering.model.AllItemMenu
import com.example.food_ordering.model.CartItem

class FavouriteAdapter(
    private val favouriteList: List<AllItemMenu>,
    private val requireContext: Context,
    private val listener: OnItemSelectedListener
) : RecyclerView.Adapter<FavouriteAdapter.FavouriteViewHolder>() {
    private val selectedItems = mutableSetOf<AllItemMenu>()

    interface OnItemSelectedListener {
        fun onItemSelected(selectedItems: Set<AllItemMenu>)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        return FavouriteViewHolder(
            FavouriteItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = favouriteList.size

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        holder.bind(favouriteList[position])
    }

    inner class FavouriteViewHolder(private val binding: FavouriteItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(favouriteItem: AllItemMenu) {
            binding.apply {
                foodNameFavorite.text = favouriteItem.foodName
                foodPriceFavorite.text = favouriteItem.foodPrice
                val uri = Uri.parse(favouriteItem.foodImage)
                Glide.with(requireContext).load(uri).override(300, 200) .into(imgFoodFavorite)

                checkBoxFavorite.setOnCheckedChangeListener(null)
                checkBoxFavorite.isChecked = selectedItems.contains(favouriteItem)
                checkBoxFavorite.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        selectedItems.add(favouriteItem)
                    } else {
                        selectedItems.remove(favouriteItem)
                        checkBoxFavorite.isChecked = false
                    }
                    listener.onItemSelected(selectedItems)
                }
            }
        }
    }
}
