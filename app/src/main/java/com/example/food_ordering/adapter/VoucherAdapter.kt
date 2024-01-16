package com.example.food_ordering.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.food_ordering.databinding.VouchersItemBinding
import com.example.food_ordering.model.AllVoucher
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class VoucherAdapter(
    private val voucherItems: List<AllVoucher>,
    private val requireContext: Context
) : RecyclerView.Adapter<VoucherAdapter.VoucherViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoucherViewHolder {
        return VoucherViewHolder(
            VouchersItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = voucherItems.size

    override fun onBindViewHolder(holder: VoucherViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class VoucherViewHolder(private val binding: VouchersItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val voucherItem = voucherItems[position]
            val database = FirebaseDatabase.getInstance().reference
            val auth: FirebaseAuth = FirebaseAuth.getInstance()
            val userId = auth.currentUser?.uid ?: ""

            binding.apply {
                code.text = voucherItem.code
                discountPercent.text = "${voucherItem.discountPercent}% OFF"
                minDiscountAmount.text = "Min. spend: ${voucherItem.minPurchaseAmount}VND"
                maxDiscount.text = "Maximum:\n ${voucherItem.maxDiscount}VND"
                exprityDate.text = "Ends on ${ voucherItem.expiryDate }"
                collectVoucher.setOnClickListener {

                    val voucherItemNew = AllVoucher(
                        userId,
                        voucherItem.code,
                        voucherItem.description,
                        voucherItem.discountAmount,
                        voucherItem.discountPercent,
                        voucherItem.expiryDate,
                        voucherItem.minPurchaseAmount,
                        voucherItem.maxDiscount,
                    )
                    database.child("accounts").child("users").child(userId).child("MyVouchers").push().setValue(voucherItemNew)
                        .addOnSuccessListener {
                            // kiểm tra xem đã thêm vào voucher chưa và hiển thị cho người dùng biết là đã thêm thành công
                            Toast.makeText(requireContext, "Item added into my voucher successfully", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {
                            Toast.makeText(requireContext, "Item added into my voucher failed", Toast.LENGTH_SHORT).show()

                        }
                }
            }
        }

    }

}