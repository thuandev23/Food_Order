package com.example.food_ordering.model
data class AllVoucher(
    val id: String? = null, // ID của voucher
    val code: String? = null, // Mã voucher
    val description: String? = null, // Mô tả về voucher
    val discountAmount: String? = null, // Số tiền giảm giá
    val discountPercent: String? = null, // Phần trăm giảm giá
    val expiryDate: String? = null, // Ngày hết hạn của voucher
    val minPurchaseAmount: String? = null, // Số tiền tối thiểu cần mua để áp dụng voucher
    val maxDiscount: String? = null, // Số tiền giảm giá tối đa
    val isUsed: Boolean = false, // Trạng thái của voucher: đã sử dụng hay chưa
)
