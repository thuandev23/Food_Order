package com.example.food_ordering.activity

import com.example.food_ordering.model.Review
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.food_ordering.R
import com.example.food_ordering.databinding.ActivityEvaluateBinding
import com.example.food_ordering.model.AllItemMenu
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EvaluateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEvaluateBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private var userId: String? = null
    private var name: String? = null
    private var image: String? = null
    private var foodName: String? = null
    private var idFood: String? = null
    private var review: String? = null
    private var rating: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEvaluateBinding.inflate(layoutInflater)
        setContentView(binding.root)


        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        foodName = intent.getStringExtra("menuItemName")
        binding.tvFoodName.text = foodName
        foodName?.let { fetchAdditionalDetails() }
        fecthUserData()
        binding.btnSubmitReview.setOnClickListener {
            submitReview()
        }
    }

    private fun fecthUserData() {
        database = FirebaseDatabase.getInstance().reference.child("accounts").child("users")
        userId = auth.currentUser?.uid
        database.child(userId!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                name = snapshot.child("name").value.toString()
                image = snapshot.child("image").value.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@EvaluateActivity, "Failed to fetch user data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchAdditionalDetails() {
        database = FirebaseDatabase.getInstance().reference.child("menu")
        database.orderByChild("foodName").equalTo(foodName).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children) {
                    val menu = data.getValue(AllItemMenu::class.java)
                    idFood = data.key
                    binding.tvFoodName.text = menu?.foodName
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@EvaluateActivity, "Failed to fetch additional details: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun submitReview() {
        database = FirebaseDatabase.getInstance().reference

        userId = auth.currentUser?.uid ?: return
        rating = binding.ratingBar.rating
        review = binding.etReview.text.toString()

        if (rating == 0f) {
            Toast.makeText(this, getString(R.string.please_select_a_rating), Toast.LENGTH_SHORT).show()
            return
        }

        if (review!!.isBlank()) {
            Toast.makeText(this, getString(R.string.please_enter_your_review), Toast.LENGTH_SHORT).show()
            return
        }

        val reviewData = Review(userId, name, image, foodName, rating, review)
        val idReview = database.child("menu").child(idFood!!).push().key
        database.child("menu").child(idFood!!).child("reviews").child(idReview!!).setValue(reviewData)
            .addOnSuccessListener {
                Toast.makeText(this, getString(R.string.review_submitted), Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to submit review: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
