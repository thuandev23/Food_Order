package com.example.food_ordering.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.food_ordering.R
import com.example.food_ordering.databinding.AllVoucherItemBinding
import com.example.food_ordering.databinding.VouchersItemBinding
import com.example.food_ordering.model.AllVoucher

class SelectVoucherAdapter(
    private val voucherItems: List<AllVoucher>,
    private val onVoucherSelected: (AllVoucher) -> Unit
) : RecyclerView.Adapter<SelectVoucherAdapter.ViewHolder>() {

    private val selectedVouchers = mutableSetOf<AllVoucher>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AllVoucherItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(voucherItems[position])
    }

    override fun getItemCount(): Int {
        return voucherItems.size
    }

    inner class ViewHolder(private val binding: AllVoucherItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(voucher: AllVoucher) {
            binding.code.text = voucher.code
            binding.discountPercent.text = "${voucher.discountPercent}% OFF"
            binding.minDiscount.text = "Min. spend: ${voucher.minPurchaseAmount}VND"
            binding.maxDiscount.text = "Maximum\n${voucher.maxDiscount}VND"
            binding.expiryDate.text = "Ends on ${voucher.expiryDate}"
            binding.root.setOnClickListener {
                if(selectedVouchers.contains(voucher)){
                    selectedVouchers.remove(voucher)
                    binding.root.setBackgroundResource(R.drawable.default_background)
                } else {
                    selectedVouchers.add(voucher)
                    binding.root.setBackgroundResource(R.drawable.selected_background)

                }
                onVoucherSelected(voucher)
            }
        }
    }
}