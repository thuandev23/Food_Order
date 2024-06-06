package com.example.food_ordering.view.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.food_ordering.R
import com.example.food_ordering.view.adapter.FavouriteAdapter
import com.example.food_ordering.databinding.FragmentFavouriteBinding
import com.example.food_ordering.model.AllItemMenu
import com.example.food_ordering.model.CartItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FavouriteFragment : Fragment(), FavouriteAdapter.OnItemSelectedListener {
    private lateinit var binding: FragmentFavouriteBinding
    private lateinit var userId: String
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private val favouriteItems = mutableListOf<AllItemMenu>()
    private lateinit var favouriteListener: ValueEventListener
    private val selectedItems = mutableSetOf<AllItemMenu>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance() // Initialize FirebaseAuth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        retrieveFavouriteItems()
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.btnProcessCart.setOnClickListener {
            addToCart(selectedItems)
        }
        binding.btnDeleteFavourites.setOnClickListener {
            deleteSelectedItems(selectedItems)
        }
        return binding.root
    }

    private fun retrieveFavouriteItems() {
        database = FirebaseDatabase.getInstance()
        userId = auth.currentUser?.uid ?: return
        val favouriteRef: DatabaseReference =
            database.reference.child("accounts").child("users").child(userId).child("Favourites")

        favouriteListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                favouriteItems.clear()
                for (voucherSnapshot in snapshot.children) {
                    val favouriteItem = voucherSnapshot.getValue(AllItemMenu::class.java)
                    favouriteItem?.let { favouriteItems.add(it) }
                }
                setAdapter(favouriteItems)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("DatabaseError", "Error: ${error.message}")
            }
        }
        favouriteRef.addValueEventListener(favouriteListener)
    }

    private fun setAdapter(favouriteItems: List<AllItemMenu>) {
        val adapter = FavouriteAdapter(favouriteItems, requireContext(), this)
        binding.favouriteRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.favouriteRecyclerView.adapter = adapter
    }

    override fun onItemSelected(selectedItems: Set<AllItemMenu>) {
        this.selectedItems.clear()
        this.selectedItems.addAll(selectedItems)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::databaseReference.isInitialized && ::favouriteListener.isInitialized) {
            databaseReference.removeEventListener(favouriteListener)
        }
    }

    private fun addToCart(items: Set<AllItemMenu>) {
        userId = auth.currentUser?.uid ?: return
        databaseReference = FirebaseDatabase.getInstance().reference
        for (item in items) {
            val addFavouriteToCartItem = CartItem(
                item.foodName.toString(),
                item.foodPrice.toString(),
                item.foodDescription.toString(),
                item.foodImage.toString(),
                item.foodIngredient.toString(),
                1
            )
            databaseReference.child("accounts").child("users").child(userId).child("CartItems").push().setValue(addFavouriteToCartItem)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(),
                        getString(R.string.added_to_cart_successfully), Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Item added into cart failed", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun deleteSelectedItems(items: Set<AllItemMenu>) {
        databaseReference = FirebaseDatabase.getInstance().reference
        userId = auth.currentUser?.uid ?: return
        val deleteFavouritesRef = database.reference.child("accounts").child("users").child(userId).child("Favourites")
        deleteFavouritesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (item in items) {
                    snapshot.children.forEach {
                        val favouriteItem = it.getValue(AllItemMenu::class.java)
                        if (favouriteItem == item) {
                            it.ref.removeValue().addOnFailureListener { exception ->
                                Log.d("DatabaseError", "Error: ${exception.message}")
                            }.addOnSuccessListener {
                                Toast.makeText(requireContext(),
                                    getString(R.string.deleted_successfully), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("DatabaseError", "Error: ${error.message}")
            }
        })
    }
}
