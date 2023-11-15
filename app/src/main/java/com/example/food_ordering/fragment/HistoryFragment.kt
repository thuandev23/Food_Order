package com.example.food_ordering.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.food_ordering.R
import com.example.food_ordering.adapter.BuyAgainAdapter
import com.example.food_ordering.databinding.FragmentHistoryBinding


class HistoryFragment : Fragment() {
private lateinit var binding: FragmentHistoryBinding
private lateinit var buyAgainAdapter: BuyAgainAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHistoryBinding.inflate(layoutInflater, container, false)
        setupRecyclerView()
        return binding.root
    }

    private fun setupRecyclerView(){
        val buyAgainFoodName = arrayListOf("Food 1", "Food 2", "Food 3")
        val buyAgainFoodPrice = arrayListOf("$7", "$13", "$10")
        val buyAgainFoodImage = arrayListOf(R.drawable.menu1,R.drawable.menu2,R.drawable.menu3)

        buyAgainAdapter = BuyAgainAdapter(buyAgainFoodName, buyAgainFoodPrice, buyAgainFoodImage)

        binding.buyAgainRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.buyAgainRecyclerView.adapter = buyAgainAdapter
    }
    companion object {}
}