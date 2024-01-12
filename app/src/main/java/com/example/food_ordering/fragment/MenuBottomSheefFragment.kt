package com.example.food_ordering.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.food_ordering.R
import com.example.food_ordering.adapter.MenuAdapter
import com.example.food_ordering.databinding.FragmentMenuBottomSheefBinding
import com.example.food_ordering.model.AllItemMenu
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MenuBottomSheefFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentMenuBottomSheefBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var menuItems: MutableList<AllItemMenu>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMenuBottomSheefBinding.inflate(inflater, container, false)
        binding.btnBackMenu.setOnClickListener {
            dismiss()
        }
        retrieveMenuItem()
        return binding.root
    }

    private fun retrieveMenuItem() {
        database = FirebaseDatabase.getInstance()
        val foodRef: DatabaseReference = database.reference.child("menu")
        menuItems = mutableListOf()
        //fetch data in firebase database
        foodRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children) {
                    val menuItem = foodSnapshot.getValue(AllItemMenu::class.java)
                    menuItem?.let { menuItems.add(it) }
                }
                setAdapter()
            }

            private fun setAdapter() {
                val adapter = MenuAdapter(menuItems, requireContext())
                binding.menuRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                binding.menuRecyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("DatabaseError", "Error: ${error.message}")
            }
        })
    }

    companion object {}
}