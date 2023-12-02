package com.example.food_ordering.model

data class CartItem (
    var foodName: String? = null,
    var foodPrice: String? = null,
    var foodDescription: String? = null,
    var foodImage: String? = null,
    var foodIngredient: String? = null,
    var foodQuantities: Int? = null,
)