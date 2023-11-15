package com.example.food_ordering.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.food_ordering.R
import com.example.food_ordering.adapter.CartAdapter
import com.example.food_ordering.databinding.FragmentCartBinding

class CartFragment : Fragment() {
    private lateinit var binding : FragmentCartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCartBinding.inflate(inflater, container, false)

        val cartFoodName = listOf("Burger", "Sandwich", "momo", "item", "check profile","Sandwich", "momo", "item", "check profile")
        val cartItemPrice = listOf("$5", "$6", "$7", "$8", "$9", "$6", "$7", "$8", "$9")
        val cartImage = listOf(R.drawable.menu1,R.drawable.menu2,R.drawable.menu3,R.drawable.menu4,R.drawable.menu5,R.drawable.menu2,R.drawable.menu3,R.drawable.menu4,R.drawable.menu5)

        val adapter = CartAdapter(ArrayList(cartFoodName), ArrayList(cartItemPrice), ArrayList(cartImage))
        binding.cartRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.cartRecyclerView.adapter = adapter

        return binding.root
    }

    companion object {

    }
}