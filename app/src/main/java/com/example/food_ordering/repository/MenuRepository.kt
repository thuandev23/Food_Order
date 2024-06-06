package com.example.food_ordering.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.food_ordering.model.AllItemMenu
import com.google.firebase.database.*

class MenuRepository {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val menuItemsLiveData = MutableLiveData<List<AllItemMenu>>()
    fun getMenuItems(): LiveData<List<AllItemMenu>> {
        val foodReference: DatabaseReference = database.reference.child("menu")
        foodReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val menuItems = mutableListOf<AllItemMenu>()
                for (foodSnapshot in snapshot.children) {
                    val menuItem = foodSnapshot.getValue(AllItemMenu::class.java)
                    menuItem?.let {
                        menuItems.add(it)
                    }
                }
                menuItemsLiveData.value = menuItems
                Log.d("MenuRepository", "Data loaded: ${menuItems.size} items")
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                Log.e("MenuRepository", "Error loading data: ${error.message}")
            }
        })
        return menuItemsLiveData
    }
}