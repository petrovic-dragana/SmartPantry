package com.cs330.smartpantry.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cs330.smartpantry.data.remote.MealApi
import com.cs330.smartpantry.data.repository.PantryRepository
import com.cs330.smartpantry.model.MealDto
import com.cs330.smartpantry.model.Recipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val repository: PantryRepository,
    private val mealApi: MealApi
) : ViewModel(){
    private val _recipes = MutableStateFlow<List<MealDto>>(emptyList())
    val recipes: StateFlow<List<MealDto>> = _recipes


    @OptIn(ExperimentalCoroutinesApi::class)
    val pantryMatchRecipes: StateFlow<List<Recipe>> = repository.getIngredients()
        .flatMapLatest { ingredients ->
            flow {
                if (ingredients.isEmpty()) {
                    emit(emptyList<Recipe>())
                } else {
                    // Uzimamo prvi sastojak iz baze
                    val mainIngredient = ingredients.first().name
                    // Pozivamo repository i odmah šaljemo rezultat u Flow
                    val meals = repository.getMealsByIngredient(mainIngredient)
                    emit(meals)
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun searchByIngredient(ingredient: String){
        viewModelScope.launch {
            val result = repository.searchRecipesByIngredient(ingredient)
            _recipes.value = result
        }
    }

    private val _selectedRecipe = MutableStateFlow<MealDto?>(null)
    val selectedRecipe: StateFlow<MealDto?> = _selectedRecipe
    fun getRecipeDetails(id: String){
        viewModelScope.launch {
            val response = mealApi.getFullRecipeDetails(id)
            _selectedRecipe.value = response.meals?.firstOrNull()
        }
    }

    fun isCurrentRecipeFavorite(id: String) = repository.isRecipeFavorite(id)

    fun toggleFavorite(meal: MealDto) {
        viewModelScope.launch {
            val favorites = repository.favoriteRecipes.first() // Uzimamo trenutnu listu iz Flow-a
            val isAlreadyFav = favorites.any { it.id == meal.idMeal }

            if (isAlreadyFav) {

                repository.deleteRecipeById(meal.idMeal)
            } else {
                val recipe = Recipe(
                    id = meal.idMeal,
                    title = meal.strMeal,
                    imageUrl = meal.strMealThumb,
                    summary = meal.strInstructions ?: ""

                )
                repository.saveRecipe(recipe)
            }
        }
    }
}