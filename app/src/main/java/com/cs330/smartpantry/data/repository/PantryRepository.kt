package com.cs330.smartpantry.data.repository

import com.cs330.smartpantry.data.local.PantryDAO
import com.cs330.smartpantry.data.remote.MealApi
import com.cs330.smartpantry.model.Ingredient
import com.cs330.smartpantry.model.MealDto
import com.cs330.smartpantry.model.Recipe
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PantryRepository @Inject constructor(
    private  val pantryDAO: PantryDAO,
    private val mealApi: MealApi
){
    //ROOM: Dobijanje svih namirnica iz baze kao Flow
    val allIngredients: Flow<List<Ingredient>> = pantryDAO.getAllIngredients()

    //API: Pretraga recepata preko mreze
    suspend fun searchRecipesByIngredient(ingredient: String): List<MealDto>{
        return try {
            val response = mealApi.getMealsByIngredient(ingredient)
            response.meals ?: emptyList()
        }catch (e: Exception){
            emptyList()
        }
    }

    //ROOM: CRUD operacije
    suspend fun addIngredient(ingredient: Ingredient){
        pantryDAO.insertIngredient(ingredient)
    }
    suspend fun removeIngredient(ingredient: Ingredient){
        pantryDAO.deleteIngredient(ingredient)
    }
    //ROOM: Omiljeni recept
    val favoriteRecipes: Flow<List<Recipe>> = pantryDAO.getFavoriteRecipes()

    suspend fun saveRecipe(recipe: Recipe){
        pantryDAO.insertRecipe(recipe)
    }

}