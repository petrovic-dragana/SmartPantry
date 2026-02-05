package com.cs330.smartpantry.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs330.smartpantry.data.repository.PantryRepository
import com.cs330.smartpantry.model.Ingredient
import com.cs330.smartpantry.model.MealDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val repository: PantryRepository
) : ViewModel(){
    private val _recipes = MutableStateFlow<List<MealDto>>(emptyList())
    val recipes: StateFlow<List<MealDto>> = _recipes

    fun searchByIngredient(ingredient: String){
        viewModelScope.launch {
            val result = repository.searchRecipesByIngredient(ingredient)
            _recipes.value = result
        }
    }
}