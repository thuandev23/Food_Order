package com.example.food_ordering.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.food_ordering.databinding.FragmentAllReviewsBinding
import com.example.food_ordering.model.Review
import com.example.food_ordering.view.adapter.ReviewAdapter
import com.example.food_ordering.viewmodel.ReviewViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AllReviewsFragment(private val foodName: String?) : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentAllReviewsBinding
    private val viewModel: ReviewViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAllReviewsBinding.inflate(inflater, container, false)
        binding.btnBackDetails.setOnClickListener {
            dismiss()
        }

        setupObservers()
        foodName?.let { viewModel.fetchAdditionalDetails(it) }

        return binding.root
    }

    private fun setupObservers() {
        viewModel.reviewsLiveData.observe(viewLifecycleOwner, Observer { reviews ->
            setReviewsAdapter(reviews)
        })

        viewModel.errorMessage.observe(viewLifecycleOwner, Observer { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        })
    }

    private fun setReviewsAdapter(reviews: List<Review>) {
        val adapter = ReviewAdapter(reviews, requireContext())
        binding.recyclerViewReviews.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewReviews.adapter = adapter
    }
}
