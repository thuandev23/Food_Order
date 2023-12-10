package com.example.food_ordering.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.food_ordering.PayOutActivity
import com.example.food_ordering.adapter.CartAdapter
import com.example.food_ordering.databinding.FragmentCartBinding
import com.example.food_ordering.model.CartItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue

class CartFragment : Fragment() {
    private lateinit var binding : FragmentCartBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var cartItemsName: MutableList<String>
    private lateinit var cartItemPrices: MutableList<String>
    private lateinit var cartImages: MutableList<String>
    private lateinit var cartDescription: MutableList<String>
    private lateinit var cartIngredient: MutableList<String>
    private lateinit var quantity: MutableList<Int>
    private lateinit var cartAdapter:CartAdapter
    private lateinit var userId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCartBinding.inflate(inflater, container, false)
        // initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()

        retrieveCartItems()

        binding.btnProcessCart.setOnClickListener{
            //get order items details before processing to check out
            getOrderCartItems()
        }
        return binding.root
    }

    private fun getOrderCartItems() {
        val orderIdReference:DatabaseReference = database.reference.child("user").child(userId).child("CartItems")
        val foodName = mutableListOf<String>()
        val foodPrice = mutableListOf<String>()
        val foodImage = mutableListOf<String>()
        val foodDescription = mutableListOf<String>()
        val foodIngredient = mutableListOf<String>()
        // get item quantities
        val foodQuantities = cartAdapter.getUpdateItemsQuantities()

        orderIdReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(foodSnapshot in snapshot.children){
                    //get the cartItems to respective list
                    val orderItems = foodSnapshot.getValue(CartItem::class.java)
                    // add item details in to list
                    orderItems?.foodName?.let { foodName.add(it) }
                    orderItems?.foodPrice?.let { foodPrice.add(it) }
                    orderItems?.foodImage?.let { foodImage.add(it) }
                    orderItems?.foodDescription?.let { foodDescription.add(it) }
                    orderItems?.foodIngredient?.let { foodIngredient.add(it) }
                }
                orderNow(foodName,foodPrice,foodImage,foodDescription,foodIngredient,foodQuantities)
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Order making failed. Please try again", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun orderNow(
        foodName: MutableList<String>,
        foodPrice: MutableList<String>,
        foodImage: MutableList<String>,
        foodDescription: MutableList<String>,
        foodIngredient: MutableList<String>,
        foodQuantities: MutableList<Int>
    ) {
            if(isAdded && context!=null){
                val intent= Intent(requireContext(), PayOutActivity::class.java)
                intent.putExtra("foodItemName", foodName as ArrayList<String>)
                intent.putExtra("foodPrice", foodPrice as ArrayList<String>)
                intent.putExtra("foodImage", foodImage as ArrayList<String>)
                intent.putExtra("foodDescription", foodDescription as ArrayList<String>)
                intent.putExtra("foodIngredient", foodIngredient as ArrayList<String>)
                intent.putExtra("foodQuantities", foodQuantities as ArrayList<Int>)
                startActivity(intent)
            }
    }

    private fun retrieveCartItems() {
        database = FirebaseDatabase.getInstance()
        userId = auth.currentUser?.uid?:""
        val foodReference: DatabaseReference = database.reference.child("user").child(userId).child("CartItems")
        cartItemsName = mutableListOf()
        cartItemPrices = mutableListOf()
        cartImages = mutableListOf()
        cartDescription = mutableListOf()
        cartIngredient = mutableListOf()
        quantity = mutableListOf()

        //fetch data form the database
        foodReference.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(foodSnapshot in snapshot.children){
                    val cartItems = foodSnapshot.getValue(CartItem::class.java)
                    // add cart item details to list
                    cartItems?.foodName?.let { cartItemsName.add(it) }
                    cartItems?.foodPrice?.let { cartItemPrices.add(it) }
                    cartItems?.foodImage?.let { cartImages.add(it) }
                    cartItems?.foodDescription?.let { cartDescription.add(it) }
                    cartItems?.foodIngredient?.let { cartIngredient.add(it) }
                    cartItems?.foodQuantities?.let { quantity.add(it) }

                }
                    setAdapter()
            }
            // Trong hàm setAdapter() của CartFragment
            private fun setAdapter() {
                if (cartItemsName.isEmpty()) {
                    binding.cartRecyclerView.visibility = View.GONE
                    binding.btnProcessCart.visibility = View.GONE
                    binding.emptyLayout.visibility = View.VISIBLE
                } else {
                    binding.cartRecyclerView.visibility = View.VISIBLE
                    binding.btnProcessCart.visibility = View.VISIBLE
                    binding.emptyLayout.visibility = View.GONE

                    cartAdapter = CartAdapter(requireContext(), cartItemsName, cartItemPrices, cartImages, cartDescription, cartIngredient, quantity)
                    binding.cartRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    binding.cartRecyclerView.adapter = cartAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Data not fetch", Toast.LENGTH_SHORT).show()
            }

        })
    }
}