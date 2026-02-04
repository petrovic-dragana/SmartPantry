package com.cs330.smartpantry.model

data class MealResponse (
    val meals: List<MealDto>?
)
data class MealDto(
    val idMeal: String,
    val strMeal: String,
    val strMealThumb: String,
    val strInstructions: String?
)