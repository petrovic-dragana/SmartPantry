package com.cs330.smartpantry.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs330.smartpantry.data.repository.CustomRecipeRepository
import com.cs330.smartpantry.model.CustomRecipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomRecipeViewModel @Inject constructor(
    private val repository: CustomRecipeRepository
): ViewModel(){
    //Interni MSF koji cuva listu recepata
    private val _recipes = MutableStateFlow<List<CustomRecipe>>(emptyList())
    //Javni SF koji UI posmatra (reaktivno posmatranje)
    val recipes: StateFlow<List<CustomRecipe>> = _recipes.asStateFlow()

    init {
        loadRecipes()
    }
    fun loadRecipes(){
        viewModelScope.launch {
            repository.getAllCustomRecipes().collect{ loadedRecipes ->
                _recipes.value = loadedRecipes

            }
        }
    }

    fun addRecipe(title: String, ingredients: String, instruction: String, imageUrl: String?){
        viewModelScope.launch {
            val newRecipe = CustomRecipe(
                title= title,
                ingredients = ingredients,
                instructions = instruction,
                imageUrl = imageUrl
            )
            repository.addRecipe(newRecipe)
            loadRecipes()
        }
    }
    fun updateRecipe(
        id: String,
        title: String,
        ingredients: String,
        instruction: String,
        imageUrl: String?
    ){
        viewModelScope.launch {
            val updatedRecipe = CustomRecipe(
                id =  id,
                title = title,
                ingredients = ingredients,
                instructions = instruction,
                imageUrl = imageUrl
            )
            repository.updateRecipe(id, updatedRecipe)
            loadRecipes()
        }
    }

    fun  deleteRecipe(id: String){
        viewModelScope.launch {
            repository.deleteRecipe(id)
            loadRecipes()
        }
    }

}