package com.example.food_ordering.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.food_ordering.databinding.FragmentMenuBottomSheefBinding
import com.example.food_ordering.model.AllItemMenu
import com.example.food_ordering.view.adapter.MenuAdapter
import com.example.food_ordering.viewmodel.MenuViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MenuBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentMenuBottomSheefBinding
    private val viewModel: MenuViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuBottomSheefBinding.inflate(inflater, container, false)
        binding.btnBackMenu.setOnClickListener {
            dismiss()
        }
        setupRecyclerView()
        setupObservers()
        return binding.root
    }

    private fun setupRecyclerView() {
        binding.menuRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupObservers() {
        viewModel.menuItems.observe(viewLifecycleOwner) { menuItems ->
            setAdapter(menuItems)
        }
    }

    private fun setAdapter(menuItems: List<AllItemMenu>) {
        val adapter = MenuAdapter(menuItems, requireContext())
        binding.menuRecyclerView.adapter = adapter
    }

    companion object {}
}
