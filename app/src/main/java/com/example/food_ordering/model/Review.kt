package com.example.food_ordering.model

data class Review(
    val userId: String? = null,
    val name: String? = null,
    val image: String? = null,
    val foodName: String? = null,
    val rating: Float = 0f,
    val review: String? = null
)
