package com.cs330.smartpantry.model

data class CustomRecipe (
    val id: String? = null,
    val title: String,
    val ingredients: String,
    val instructions: String,
    val imageUrl: String? = null
)