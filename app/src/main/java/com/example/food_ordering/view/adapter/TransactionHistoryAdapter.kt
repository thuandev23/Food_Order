package com.example.food_ordering.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.food_ordering.R
import com.example.food_ordering.databinding.TransactionHistoryItemBinding
import com.example.food_ordering.model.OrderDetails
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionHistoryAdapter (private val transItems: List<OrderDetails>, private val requireContext: Context)
    : RecyclerView.Adapter<TransactionHistoryAdapter.TransactionHistoryViewHolder >() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionHistoryAdapter.TransactionHistoryViewHolder {
        return TransactionHistoryViewHolder(
            TransactionHistoryItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = transItems.size

    override fun onBindViewHolder(holder: TransactionHistoryViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class TransactionHistoryViewHolder(private val binding: TransactionHistoryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                codePurchase.text =
                    requireContext.getString(
                        R.string.purchase_code,
                        transItems[position].itemPushKey
                    )
                val desc = "Address: ${transItems[position].address}; Phone Number: ${transItems[position].phoneNumber}; Items: ${transItems[position].foodNames?.joinToString(", ")}; Total Price: ${transItems[position].totalPrice}."
                description.text = desc
                totalMoney.text = transItems[position].totalPrice +" VND"
                if (transItems[position].payToMerchant){
                    status.text = requireContext.getString(R.string.success_paid)
                } else {
                    status.text = requireContext.getString(R.string.not_paid)

                }
                binding.dateTime.text = convertTimestampToDateString(transItems[position].currentTime)
            }
        }
    }
    private fun convertTimestampToDateString(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy - HH:mm:ss") // Adjusted format
        val date = Date(timestamp)  // Use timestamp directly
        return sdf.format(date)
    }
}