package com.example.food_ordering.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.food_ordering.databinding.CartItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartAdapter(
    private val context: Context,
    private var cartItems: MutableList<String>,
    private var cartItemPrices: MutableList<String>,
    private var cartImages: MutableList<String>,
    private var cartDescription: MutableList<String>,
    private var cartIngredient: MutableList<String>,
    private var cartQuantity: MutableList<Int>,
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {
    // initialize FirebaseAuth
    private val auth = FirebaseAuth.getInstance()

    init {
        val database = FirebaseDatabase.getInstance()
        val userId = auth.currentUser?.uid ?: ""
        val cartItemsNumber = cartItems.size
        itemQuantities = IntArray(cartItemsNumber) { 1 }
        cartItemsReference = database.reference.child("user").child(userId).child("CartItems")
    }

    companion object {
        private var itemQuantities: IntArray = intArrayOf()
        private lateinit var cartItemsReference: DatabaseReference
    }

    inner class CartViewHolder(private val binding: CartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                val quantity = itemQuantities[position]
                cartFoodName.text = cartItems[position]
                cartPrice.text = cartItemPrices[position]
                // load image using Glide
                val uri = Uri.parse(cartImages[position])
                Glide.with(context).load(uri).into(imageFoodCart)
                countItemCart.text = quantity.toString()

                btnMinusCart.setOnClickListener {
                    decreaseQuantity(position)
                }

                btnPlusCart.setOnClickListener {
                    increaseQuantity(position)
                }

                btnDeleteTrashCart.setOnClickListener {
                    val itemPosition = adapterPosition
                    if (itemPosition != RecyclerView.NO_POSITION) {
                        deleteItem(itemPosition)
                    }
                }

            }
        }

        private fun decreaseQuantity(position: Int) {
            if (itemQuantities[position] > 1) {
                itemQuantities[position]--
                cartQuantity[position] = itemQuantities[position]
                binding.countItemCart.text = itemQuantities[position].toString()
            }
        }
        private fun increaseQuantity(position: Int) {
            if (itemQuantities[position] < 10) {
                itemQuantities[position]++
                cartQuantity[position] = itemQuantities[position]
                binding.countItemCart.text = itemQuantities[position].toString()
            }
        }

        private fun deleteItem(position: Int) {
            getUniqueKeyPosition(position) { uniqueKey ->
                if (uniqueKey != null) {
                    removeItem(position, uniqueKey)
                }
            }
        }

        private fun removeItem(position: Int, uniqueKey: String) {
            if (uniqueKey != null) {
                cartItemsReference.child(uniqueKey).removeValue().addOnSuccessListener {
                    if (position >= 0 && position < cartItems.size) {
                        cartItems.removeAt(position)
                        cartImages.removeAt(position)
                        cartDescription.removeAt(position)
                        cartIngredient.removeAt(position)
                        cartItemPrices.removeAt(position)

                        // Update cartQuantity
                        if (position < cartQuantity.size) {
                            cartQuantity.removeAt(position)
                        }

                        Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show()

                        // Update itemQuantities
                        itemQuantities =
                            itemQuantities.filterIndexed { index, _ -> index != position }.toIntArray()

                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, cartItems.size)
                    }
                }.addOnFailureListener {
                    Toast.makeText(context, "Delete failed", Toast.LENGTH_SHORT).show()
                }
            }
        }


        private fun getUniqueKeyPosition(position: Int, onComplete: (String?) -> Unit) {
            cartItemsReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var uniqueKey: String? = null
                    snapshot.children.forEachIndexed { index, dataSnapshot ->
                        if (index == position) {
                            uniqueKey = dataSnapshot.key
                            return@forEachIndexed
                        }
                    }
                    onComplete(uniqueKey)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun getItemCount(): Int = cartItems.size

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(position)
    }

    // get update quantity
    fun getUpdateItemsQuantities(): MutableList<Int> {
        val itemQuantity = mutableListOf<Int>()
        itemQuantity.addAll(cartQuantity)
        return itemQuantity
    }

}