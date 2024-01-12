package com.example.food_ordering.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.food_ordering.databinding.AllVoucherItemBinding
import com.example.food_ordering.model.AllVoucher

class MyVoucherAdapter(
    private val voucherItems: List<AllVoucher>,
    private val requireContext: Context
) : RecyclerView.Adapter<MyVoucherAdapter.MyVoucherViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyVoucherViewHolder {
        return MyVoucherViewHolder(
            AllVoucherItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
    override fun getItemCount(): Int = voucherItems.size

    override fun onBindViewHolder(holder: MyVoucherViewHolder, position: Int) {
        holder.bind(position)
    }
    inner class MyVoucherViewHolder(private val binding: AllVoucherItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val voucherItem = voucherItems[position]
            binding.apply {
                code.text = voucherItem.code
                description.text = voucherItem.description
                expiryDate.text = voucherItem.expiryDate
                idVoucher.text = voucherItem.id
                minDiscount.text = "Min, spend: ${voucherItem.minPurchaseAmount}"
                maxDiscount.text = "Max discount: ${voucherItem.maxDiscount}"
            }
        }
    }

}