package com.example.food_ordering.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.food_ordering.model.AllItemMenu
import com.example.food_ordering.repository.MenuRepository
import java.text.Normalizer
import java.util.regex.Pattern

class MenuViewModel : ViewModel() {
    private val repository: MenuRepository = MenuRepository()
    private val _filteredMenuItems = MutableLiveData<List<AllItemMenu>>()
    val filteredMenuItems: LiveData<List<AllItemMenu>> get() = _filteredMenuItems

    val menuItems: LiveData<List<AllItemMenu>> = repository.getMenuItems()

    fun filterMenuItems(query: String) {
        val normalizedQuery = query.normalize()
        val filteredList = menuItems.value?.filter {
            it.foodName?.normalize()?.contains(normalizedQuery) == true
        }
        _filteredMenuItems.value = filteredList ?: listOf()
    }
    private fun String.normalize(): String {
        val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
        return pattern.matcher(temp).replaceAll("").lowercase()
    }
}
