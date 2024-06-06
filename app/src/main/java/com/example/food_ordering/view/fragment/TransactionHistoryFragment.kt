package com.example.food_ordering.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.food_ordering.R
import com.example.food_ordering.databinding.ActivityMyVoucherBinding
import com.example.food_ordering.databinding.FragmentAllReviewsBinding
import com.example.food_ordering.databinding.FragmentTransactionHistoryBinding
import com.example.food_ordering.model.OrderDetails
import com.example.food_ordering.model.Review
import com.example.food_ordering.view.adapter.ReviewAdapter
import com.example.food_ordering.view.adapter.TransactionHistoryAdapter
import com.example.food_ordering.viewmodel.TransactionHistoryViewModel


class TransactionHistoryFragment : Fragment() {
    private lateinit var binding: FragmentTransactionHistoryBinding
    private val viewModel: TransactionHistoryViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTransactionHistoryBinding.inflate(inflater, container, false)

        binding.imgBtnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        viewModel.fetchTransactionHistory()
        setupObservers()
        return binding.root
    }
    private fun setupObservers() {
        viewModel.transactionHistory.observe(viewLifecycleOwner, Observer { trans ->
            setReviewsAdapter(trans)
        })

        viewModel.errorMessage.observe(viewLifecycleOwner, Observer { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        })
    }

    private fun setReviewsAdapter(trans: List<OrderDetails>) {
        val adapter = TransactionHistoryAdapter(trans, requireContext())
        binding.transactionHistoryRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.transactionHistoryRecyclerView.adapter = adapter
    }
}