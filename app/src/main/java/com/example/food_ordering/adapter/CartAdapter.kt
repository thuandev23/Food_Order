package com.example.food_ordering.adapter

import android.app.AlertDialog
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.net.Uri
import android.provider.Settings.Global.getString
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.food_ordering.R
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
    private val database = FirebaseDatabase.getInstance()
    private val userUid = auth.currentUser?.uid ?: ""
    private val cartItemsReference = database.reference.child("accounts").child("users").child(userUid).child("CartItems")
    private var  itemQuantities = IntArray(cartItems.size) {1}
    init {
        itemQuantities = cartQuantity.toIntArray()
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

            }
        }
        private fun decreaseQuantity(position: Int) {
            if (itemQuantities[position] > 1) {
                itemQuantities[position]--
                cartQuantity[position] = itemQuantities[position]
                binding.countItemCart.text = itemQuantities[position].toString()
                updateQuantityInDatabase(position)
            }
        }

        private fun increaseQuantity(position: Int) {
            if (itemQuantities[position] < 10) {
                itemQuantities[position]++
                cartQuantity[position] = itemQuantities[position]
                binding.countItemCart.text = itemQuantities[position].toString()
                updateQuantityInDatabase(position)
            }
        }
        private fun updateQuantityInDatabase(position: Int) {
            getUniqueKeyPosition(position) { uniqueKey ->
                if (uniqueKey != null) {
                    cartItemsReference.child(uniqueKey).child("foodQuantities").setValue(itemQuantities[position])
                }
            }
        }
    }
    fun getUpdateItemsQuantities(): MutableList<Int> {
        return itemQuantities.toMutableList()
    }

    private fun deleteItem(position: Int) {
            getUniqueKeyPosition(position) { uniqueKey ->
                if (uniqueKey != null) {
                    removeItem(position, uniqueKey)
                }
            }
        }

    private fun removeItem(position: Int, uniqueKey: String) {
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

    fun setItemTouchHelper(recyclerView: RecyclerView) {
        val itemTouchHelperCallback = object :
            ItemTouchHelper.SimpleCallback(0,  ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.delete_confirmation))
                    .setMessage(context.getString(R.string.delete_message) + cartItems[position])
                    .setPositiveButton(context.getString(R.string.yes)) { _, _ ->
                        deleteItem(position) // Uncomment and implement this method
                        Toast.makeText(context,context.getString(R.string.deleted), Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton(context.getString(R.string.no)) { _, _ ->
                        // Revert swipe
                        notifyItemChanged(viewHolder.adapterPosition)
                    }
                    .show()

            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                val itemView = viewHolder.itemView
                val background = Paint()
                background.color = ContextCompat.getColor(context, R.color.red)
                if (dX > 0) {
                    c.drawRect(itemView.left.toFloat(), itemView.top.toFloat(), dX, itemView.bottom.toFloat(), background)
                    val text = context.getString(R.string.delete)
                    val textMargin = 20
                    val textPaint = Paint()
                    textPaint.color = Color.WHITE
                    textPaint.textSize = 40f
                    c.drawText(text, itemView.left.toFloat() + textMargin +50, itemView.top.toFloat() + itemView.height.toFloat() / 2 + textMargin, textPaint)

                }
            }
        }


        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun getItemCount(): Int = cartItems.size

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(position)
    }

}