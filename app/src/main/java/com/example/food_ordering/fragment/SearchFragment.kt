package com.example.food_ordering.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.food_ordering.R
import com.example.food_ordering.adapter.MenuAdapter
import com.example.food_ordering.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: MenuAdapter
    private val originalMenuFoodName = listOf(
        "Burger",
        "Sandwich",
        "momo",
        "item",
        "check profile",
        "Sandwich",
        "momo",
        "item",
        "check profile"
    )
    private val originalMenuFoodPrice = listOf("$5", "$6", "$7", "$8", "$9", "$6", "$7", "$8", "$9")
    private val originalMenuFoodImage = listOf(
        R.drawable.menu1,
        R.drawable.menu2,
        R.drawable.menu3,
        R.drawable.menu4,
        R.drawable.menu5,
        R.drawable.menu2,
        R.drawable.menu3,
        R.drawable.menu4,
        R.drawable.menu5
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private val filteredMenuFoodName = mutableListOf<String>()
    private val filteredMenuFoodPrice = mutableListOf<String>()
    private val filteredMenuFoodImage = mutableListOf<Int>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        adapter = MenuAdapter(filteredMenuFoodName, filteredMenuFoodPrice, filteredMenuFoodImage, requireContext())
        binding.menuSearchRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.menuSearchRecyclerView.adapter = adapter

        setupSearchView()
        showAllMenu()
        return binding.root
    }

    private fun showAllMenu() {
        filteredMenuFoodName.clear()
        filteredMenuFoodPrice.clear()
        filteredMenuFoodImage.clear()

        filteredMenuFoodName.addAll(originalMenuFoodName)
        filteredMenuFoodPrice.addAll(originalMenuFoodPrice)
        filteredMenuFoodImage.addAll(originalMenuFoodImage)
        adapter.notifyDataSetChanged()
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                filterMenuItems(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filterMenuItems(newText)
                return true
            }
        })
    }

    private fun filterMenuItems(query: String) {
        val tempFilteredMenuFoodName = mutableListOf<String>()
        val tempFilteredMenuFoodPrice = mutableListOf<String>()
        val tempFilteredMenuFoodImage = mutableListOf<Int>()

        originalMenuFoodName.forEachIndexed { index, foodName ->
            if (foodName.contains(query, ignoreCase = true)) {
                tempFilteredMenuFoodName.add(foodName)
                tempFilteredMenuFoodPrice.add(originalMenuFoodPrice[index])
                tempFilteredMenuFoodImage.add(originalMenuFoodImage[index])
            }
        }

        // Clear the previous data and add the filtered data
        filteredMenuFoodName.clear()
        filteredMenuFoodPrice.clear()
        filteredMenuFoodImage.clear()

        filteredMenuFoodName.addAll(tempFilteredMenuFoodName)
        filteredMenuFoodPrice.addAll(tempFilteredMenuFoodPrice)
        filteredMenuFoodImage.addAll(tempFilteredMenuFoodImage)

        adapter.notifyDataSetChanged()
    }


    companion object {}
}



