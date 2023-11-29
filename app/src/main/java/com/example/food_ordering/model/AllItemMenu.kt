package com.example.food_ordering.model

data class AllItemMenu(
    val foodName:String ?= null,
    val foodPrice:String ?= null,
    val foodDescription:String ?= null,
    val foodImage:String ?= null,
    val foodIngredient:String ?= null,
)
