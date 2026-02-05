package com.cs330.smartpantry.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs330.smartpantry.data.repository.PantryRepository
import com.cs330.smartpantry.model.Ingredient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PantryViewModel @Inject constructor(
    private val repository: PantryRepository
) : ViewModel() {
    //Prikazujemo listu namirnica kao Flow koji Compose moze da posmatra
    val ingredients: StateFlow<List<Ingredient>> = repository.allIngredients
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    fun addIngredient(name: String, quantity: Double, unit: String){
        viewModelScope.launch {
            repository.addIngredient(Ingredient(name = name, quantity = quantity, unit = unit))
        }
    }
    fun deleteIngredient(ingredient: Ingredient){
        viewModelScope.launch {
            repository.removeIngredient(ingredient)
        }
    }


}