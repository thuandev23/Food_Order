package com.example.food_ordering.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.food_ordering.databinding.FragmentSearchBinding
import com.example.food_ordering.view.adapter.MenuAdapter
import com.example.food_ordering.viewmodel.MenuViewModel

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: MenuAdapter
    private val viewModel: MenuViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)


        setupSearchView()
        setupObservers()
        setupRecyclerView()
        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = MenuAdapter(emptyList(), requireContext())
        binding.menuSearchRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.menuSearchRecyclerView.adapter = adapter
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.filterMenuItems(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.filterMenuItems(newText)
                return true
            }
        })
    }

    private fun setupObservers() {
        viewModel.menuItems.observe(viewLifecycleOwner) { menuItems ->
            adapter.updateMenuItems(menuItems)
        }
        viewModel.filteredMenuItems.observe(viewLifecycleOwner) { filteredItems ->
            adapter.updateMenuItems(filteredItems)
        }
    }
}
