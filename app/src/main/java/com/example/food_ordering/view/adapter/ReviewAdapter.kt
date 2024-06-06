package com.example.food_ordering.view.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.food_ordering.databinding.ReviewItemBinding
import com.example.food_ordering.model.Review

class ReviewAdapter(private val reviewItems: List<Review>, private val requireContext: Context) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        return ReviewViewHolder(
            ReviewItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = reviewItems.size

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ReviewViewHolder(private val binding: ReviewItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                nameUser.text = reviewItems[position].name
                val uri = Uri.parse(reviewItems[position].image)
                Glide.with(requireContext).load(uri).into(imgUser)
                reviewDes.text = reviewItems[position].review
                ratingBar.rating = reviewItems[position].rating
            }
        }
    }
}