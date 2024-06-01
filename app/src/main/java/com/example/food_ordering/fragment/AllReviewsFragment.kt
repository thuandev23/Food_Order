package com.example.food_ordering.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.food_ordering.adapter.ReviewAdapter
import com.example.food_ordering.databinding.FragmentAllReviewsBinding
import com.example.food_ordering.model.Review
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AllReviewsFragment(private val foodName: String?) : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentAllReviewsBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var allReviewItems: MutableList<Review>
    private lateinit var auth: FirebaseAuth
    private var idFood: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAllReviewsBinding.inflate(inflater, container, false)
        binding.btnBackDetails.setOnClickListener {
            dismiss()
        }
        auth = FirebaseAuth.getInstance()
        fetchAdditionalDetails()
        return binding.root
    }

    private fun fetchAdditionalDetails() {
        val userId = auth.currentUser?.uid ?: return
        database = FirebaseDatabase.getInstance()
        database.reference.child("menu").orderByChild("foodName").equalTo(foodName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (itemSnapshot in snapshot.children) {
                        idFood = itemSnapshot.key
                        // Fetch reviews after getting the idFood
                        setAdapterReviews(idFood!!)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Failed to fetch menu: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
    private fun setAdapterReviews(menuItemId: String) {
        auth.currentUser?.uid ?: return
        database = FirebaseDatabase.getInstance()
        database.reference.child("menu").child(menuItemId).child("reviews").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val reviews = mutableListOf<Review>()
                for (reviewSnapshot in snapshot.children) {
                    val review = reviewSnapshot.getValue(Review::class.java)
                    if (review != null && review.foodName == foodName) {
                        reviews.add(review)
                    }
                }
                setReviewsAdapter(reviews)
            }

            private fun setReviewsAdapter(reviews: MutableList<Review>) {
                val adapter = ReviewAdapter(reviews, requireContext())
                binding.recyclerViewReviews.layoutManager = LinearLayoutManager(requireContext())
                binding.recyclerViewReviews.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to fetch reviews: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}