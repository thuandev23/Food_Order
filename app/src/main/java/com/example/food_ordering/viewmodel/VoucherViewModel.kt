package com.example.food_ordering.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.food_ordering.model.AllVoucher
import com.example.food_ordering.repository.VoucherRepository

class VoucherViewModel : ViewModel(){
    private val voucherRepository: VoucherRepository = VoucherRepository()
    val vouchers: LiveData<List<AllVoucher>> = voucherRepository.getVouchers()
}