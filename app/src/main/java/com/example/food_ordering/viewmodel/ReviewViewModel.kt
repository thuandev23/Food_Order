package com.example.food_ordering.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.food_ordering.model.Review
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ReviewViewModel(application: Application) : AndroidViewModel(application) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val context = getApplication<Application>().applicationContext

    val reviewsLiveData = MutableLiveData<List<Review>>()
    val errorMessage = MutableLiveData<String>()

    fun fetchAdditionalDetails(foodName: String) {
        val userId = auth.currentUser?.uid ?: return
        database.reference.child("menu").orderByChild("foodName").equalTo(foodName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (itemSnapshot in snapshot.children) {
                        val idFood = itemSnapshot.key
                        if (idFood != null) {
                            fetchReviews(idFood, foodName)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    errorMessage.value = "Failed to fetch menu: ${error.message}"
                }
            })
    }

    private fun fetchReviews(menuItemId: String, foodName: String) {
        database.reference.child("menu").child(menuItemId).child("reviews")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val reviews = mutableListOf<Review>()
                    for (reviewSnapshot in snapshot.children) {
                        val review = reviewSnapshot.getValue(Review::class.java)
                        if (review != null && review.foodName == foodName) {
                            reviews.add(review)
                        }
                    }
                    reviewsLiveData.value = reviews
                }

                override fun onCancelled(error: DatabaseError) {
                    errorMessage.value = "Failed to fetch reviews: ${error.message}"
                }
            })
    }
}
