package com.cs330.smartpantry.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey val id: Int, // Koristićemo ID sa API-ja
    val title: String,
    val imageUrl: String,
    val summary: String,
    val isFavorite: Boolean = false
)