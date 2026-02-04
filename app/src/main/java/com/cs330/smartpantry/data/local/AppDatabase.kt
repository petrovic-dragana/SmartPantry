package com.cs330.smartpantry.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cs330.smartpantry.model.Ingredient
import com.cs330.smartpantry.model.Recipe

@Database(entities = [Ingredient::class, Recipe::class], version = 1)
abstract class AppDatabase : RoomDatabase(){
    abstract fun pantryDao():PantryDAO
}