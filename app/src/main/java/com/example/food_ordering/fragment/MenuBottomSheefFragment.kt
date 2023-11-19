package com.example.food_ordering.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.food_ordering.R
import com.example.food_ordering.adapter.MenuAdapter
import com.example.food_ordering.databinding.FragmentMenuBottomSheefBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MenuBottomSheefFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentMenuBottomSheefBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMenuBottomSheefBinding.inflate(inflater, container, false)
        binding.btnBackMenu.setOnClickListener{
            dismiss()
        }
        val menuFoodName = listOf(
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
        val menuItemPrice = listOf("$5", "$6", "$7", "$8", "$9", "$6", "$7", "$8", "$9")
        val menuImage = listOf(
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

        val adapter =MenuAdapter(
            ArrayList(menuFoodName),
            ArrayList(menuItemPrice),
            ArrayList(menuImage),
            requireContext()
        )
        binding.menuRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.menuRecyclerView.adapter = adapter
        return binding.root
    }

    companion object {}
}