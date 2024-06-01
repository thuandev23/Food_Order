package com.example.food_ordering.activity

import com.example.food_ordering.model.Review
import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.food_ordering.R
import com.example.food_ordering.databinding.ActivityDetailsBinding
import com.example.food_ordering.fragment.AllReviewsFragment
import com.example.food_ordering.model.AllItemMenu
import com.example.food_ordering.model.CartItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding

    private var idFood: String? = null
    private var foodName: String? = null
    private var foodPrice: String? = null
    private var foodDescription: String? = null
    private var foodImage: String? = null
    private var foodIngredient: String? = null

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var isFavourite = false
    private lateinit var favouriteRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()

        binding.btnBackDetails.setOnClickListener {
            finish()
        }

        foodName = intent.getStringExtra("menuItemName")
        foodPrice = intent.getStringExtra("menuItemPrice")
        foodDescription = intent.getStringExtra("menuItemDescription")
        foodImage = intent.getStringExtra("menuItemImage")
        foodIngredient = intent.getStringExtra("menuItemIngredient")
        foodName?.let { fetchAdditionalDetails() }
        with(binding) {
            foodNameDetails.text = foodName
            foodDescriptionDetails.text = foodDescription
            foodIngredientDetails.text = foodIngredient
            foodImage?.let {
                Glide.with(this@DetailsActivity).load(Uri.parse(it)).into(foodImageDetails)
            }
        }

        // Check if the item is already in favourites
        checkIfItemIsFavourite()

        binding.btnAddToCart.setOnClickListener {
            addItemToCart()
        }

        binding.btnFavouriteDetails.setOnClickListener {
            if (isFavourite) {
                removeItemFromFavourite()
            } else {
                addItemToFavourite()
            }
        }

        binding.clickViewAllReviews.setOnClickListener {
            val allReviewsFragment = AllReviewsFragment(foodName)
            allReviewsFragment.show(supportFragmentManager, allReviewsFragment.tag)
        }
    }
    private fun fetchAdditionalDetails() {
        val userId = auth.currentUser?.uid ?: return
        database = FirebaseDatabase.getInstance().reference.child("menu")
        database.orderByChild("foodName").equalTo(foodName).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (itemSnapshot in snapshot.children) {
                    val item = itemSnapshot.getValue(AllItemMenu::class.java)
                    idFood = itemSnapshot.key
                    item?.let {
                        with(binding) {
                            foodNameDetails.text = item.foodName
                            foodDescriptionDetails.text = item.foodDescription
                            foodIngredientDetails.text = item.foodIngredient
                            Glide.with(this@DetailsActivity).load(Uri.parse(item.foodImage)).into(foodImageDetails)
                        }
                        // Fetch reviews after getting the idFood
                        fetchRatingsAndReviews(idFood!!)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DetailsActivity, "Failed to fetch additional details: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchRatingsAndReviews(menuItemId: String) {
        database = FirebaseDatabase.getInstance().reference
        database.child("menu").child(menuItemId).child("reviews").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalRating = 0f
                var totalReviews = 0

                for (reviewSnapshot in snapshot.children) {
                    val review = reviewSnapshot.getValue(Review::class.java)
                    if (review != null && review.foodName == foodName) {
                        totalRating += review.rating
                        totalReviews++
                    }
                }

                if (totalReviews > 0) {
                    val averageRating = totalRating / totalReviews
                    binding.ratingBarDetails.rating = averageRating
                    binding.ratingTotal.text = String.format("%.2f", averageRating)
                    binding.tvTotalReviews.text = "($totalReviews reviews)"
                }
                else {
                    binding.ratingBarDetails.rating = 0f
                    binding.ratingTotal.text = ""
                    binding.tvTotalReviews.text = "(0 reviews)"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DetailsActivity, "Failed to fetch ratings and reviews: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun checkIfItemIsFavourite() {
        val userId = auth.currentUser?.uid ?: return
        favouriteRef = FirebaseDatabase.getInstance().reference.child("accounts").child("users").child(userId).child("Favourites")

        favouriteRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                isFavourite = false
                for (favouriteSnapshot in snapshot.children) {
                    val favouriteItem = favouriteSnapshot.getValue(AllItemMenu::class.java)
                    if (favouriteItem != null && favouriteItem.foodName == foodName) {
                        isFavourite = true
                        break
                    }
                }
                updateFavouriteButton()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DetailsActivity, "Failed to check favourites: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateFavouriteButton() {
        if (isFavourite) {
            binding.btnFavouriteDetails.setImageResource(R.drawable.lover)
        } else {
            binding.btnFavouriteDetails.setImageResource(R.drawable.heart)
        }
    }

    private fun addItemToFavourite() {
        val userId = auth.currentUser?.uid ?: return
        database = FirebaseDatabase.getInstance().reference
        val favouriteItem = AllItemMenu(
            foodName.toString(),
            foodPrice.toString(),
            foodDescription.toString(),
            foodImage.toString(),
            foodIngredient.toString()
        )
        // Use coroutines to perform the Firebase operation on a background thread
        CoroutineScope(Dispatchers.IO).launch {
            try {
                withContext(Dispatchers.IO) {
                    database.child("accounts").child("users").child(userId).child("Favourites").push().setValue(favouriteItem).await()
                }
                withContext(Dispatchers.Main) {
                    isFavourite = true
                    updateFavouriteButton()
                    AlertDialog.Builder(this@DetailsActivity).apply {
                        setTitle("Success")
                        setMessage("Do you want to add to the Cart?")
                        setPositiveButton("Yes") { _, _ ->
                            addItemToCart()
                        }
                        setNegativeButton("No") { _, _ -> }
                    }.create().show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DetailsActivity, "Add to favourite failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun removeItemFromFavourite() {
        val userId = auth.currentUser?.uid ?: return

        favouriteRef = FirebaseDatabase.getInstance().reference.child("accounts").child("users").child(userId).child("Favourites")

        favouriteRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (favouriteSnapshot in snapshot.children) {
                    val favouriteItem = favouriteSnapshot.getValue(AllItemMenu::class.java)
                    if (favouriteItem != null && favouriteItem.foodName == foodName) {
                        favouriteSnapshot.ref.removeValue()
                            .addOnSuccessListener {
                                isFavourite = false
                                updateFavouriteButton()
                                Toast.makeText(this@DetailsActivity, "Removed from favourites", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this@DetailsActivity, "Remove from favourite failed: ${it.message}", Toast.LENGTH_SHORT).show()
                            }
                        break
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DetailsActivity, "Failed to remove from favourites: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addItemToCart() {
        val userId = auth.currentUser?.uid ?: return
        database = FirebaseDatabase.getInstance().reference
        val cartItem = CartItem(
            foodName.toString(),
            foodPrice.toString(),
            foodDescription.toString(),
            foodImage.toString(),
            foodIngredient.toString(),
            1
        )
        database.child("accounts").child("users").child(userId).child("CartItems").push().setValue(cartItem)
            .addOnSuccessListener {
                Toast.makeText(this, "Item added into cart successfully", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, "Item added into cart failed", Toast.LENGTH_SHORT).show()
            }
    }
}
