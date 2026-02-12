package com.cs330.smartpantry.ui.screens

import android.icu.text.StringSearch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cs330.smartpantry.data.remote.MealApi
import com.cs330.smartpantry.data.repository.CustomRecipeRepository
import com.cs330.smartpantry.data.repository.PantryRepository
import com.cs330.smartpantry.model.MealDto
import com.cs330.smartpantry.model.Recipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.http.Query
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val repository: PantryRepository,
    private val mealApi: MealApi,
    private val customRepository: CustomRecipeRepository
) : ViewModel(){

    private val _recipes = MutableStateFlow<List<MealDto>>(emptyList())
    val recipes: StateFlow<List<MealDto>> = _recipes

    fun searchByIngredient(ingredient: String){
        viewModelScope.launch {
            val result = repository.searchRecipesByIngredient(ingredient)
            _recipes.value = result
        }
    }
    fun searchRecipes(query: String, searchByName: Boolean){
        viewModelScope.launch {
            val result = if(searchByName){
                repository.searchRecipesByName(query)
            }else{
                repository.searchRecipesByIngredient(query)
            }
            _recipes.value = result
        }
    }

    private val _selectedRecipe = MutableStateFlow<MealDto?>(null)
    val selectedRecipe: StateFlow<MealDto?> = _selectedRecipe
//    fun getRecipeDetails(id: String){
//        viewModelScope.launch {
//            val response = mealApi.getFullRecipeDetails(id)
//            _selectedRecipe.value = response.meals?.firstOrNull()
//        }
//    }

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
                    imageUrl = meal.strMealThumb ?: "",
                    summary = meal.strInstructions ?: ""

                )
                repository.saveRecipe(recipe)
            }
        }
    }
    val favoriteRecipes: StateFlow<List<Recipe>> = repository.favoriteRecipes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    fun removeFromFavorites(recipe: Recipe) {
        viewModelScope.launch {
            repository.removeRecipe(recipe)
        }
    }
    val favoriteRecipesIds: StateFlow<Set<String>> = repository.favoriteRecipes
        .map { list -> list.map { it.id }.toSet() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )
    @OptIn(ExperimentalCoroutinesApi::class)
    val pantryMatchRecipes: StateFlow<List<Recipe>> = repository.getIngredients()
        .flatMapLatest { ingredients ->
            flow {
                if (ingredients.isEmpty()) {
                    emit(emptyList<Recipe>())
                } else {
                    // Uzimamo prvi sastojak iz baze
                    val mainIngredient = ingredients.first().name
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

    private val _selectedCategory = MutableStateFlow("Seafood")
    val selectedCategory: StateFlow<String> = _selectedCategory
    @OptIn(ExperimentalCoroutinesApi::class)
    val categoryRecipes: StateFlow<List<Recipe>> = _selectedCategory
        .flatMapLatest { category ->
            flow {
                val result = repository.getMealsByCategory(category) // Napravi ovu metodu u repository-u
                emit(result)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }
    private val _searchQuery = MutableStateFlow("")
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(600) // Čeka 600ms od poslednjeg otkucanog slova
                .filter { it.length > 2 } // Ne traži ako je upisano manje od 3 slova
                .collect { query ->
                    performSearch(query)
                }
        }
    }

    fun onQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.searchRecipesByName(query)
            _recipes.value = result
            _isLoading.value = false
        }
    }
    fun getRecipeDetails(id: String) {
        viewModelScope.launch {
            try {
                val response = mealApi.getFullRecipeDetails(id)
                val apiMeal = response.meals?.firstOrNull()

                if (apiMeal != null) {
                    _selectedRecipe.value = apiMeal
                } else {
                    loadCustomRecipeAsMealDto(id)
                }
            } catch (e: Exception) {
                loadCustomRecipeAsMealDto(id)
            }
        }
    }

    private suspend fun loadCustomRecipeAsMealDto(id: String) {
        // Uzimamo listu iz Flow-a i tražimo recept
//        val customRecipe = customRepository.getAllCustomRecipes().first().find { it.id == id }
        val recipes = customRepository.getAllCustomRecipes().firstOrNull() ?: emptyList()
        val customRecipe = recipes.find { it.id == id }

        if (customRecipe != null) {
            val ingredientsList = customRecipe.ingredients?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()

            _selectedRecipe.value = MealDto(
                idMeal = customRecipe.id ?: "",
                strMeal = customRecipe.title ?: "",
                strInstructions = customRecipe.instructions ?: "",
                strMealThumb = customRecipe.imageUrl,

                strIngredient1 = ingredientsList.getOrNull(0) ?: "",
                strIngredient2 = ingredientsList.getOrNull(1) ?: "",
                strIngredient3 = ingredientsList.getOrNull(2) ?: "",
                strIngredient4 = ingredientsList.getOrNull(3) ?: "",
                strIngredient5 = ingredientsList.getOrNull(4) ?: "",
                strIngredient6 = ingredientsList.getOrNull(5) ?: "",
                strIngredient7 = ingredientsList.getOrNull(6) ?: "",
                strIngredient8 = ingredientsList.getOrNull(7) ?: "",
                strIngredient9 = ingredientsList.getOrNull(8) ?: "",
                strIngredient10 = ingredientsList.getOrNull(9) ?: "",

                strMeasure1 = if (ingredientsList.getOrNull(0) != null) "Personal choice" else "",
                strMeasure2 = if (ingredientsList.getOrNull(1) != null) "Personal choice" else "",
                strMeasure3 = if (ingredientsList.getOrNull(2) != null) "Personal choice" else "",
                strMeasure4 = if (ingredientsList.getOrNull(3) != null) "Personal choice" else "",
                strMeasure5 = if (ingredientsList.getOrNull(4) != null) "Personal choice" else "",
                strMeasure6 = if (ingredientsList.getOrNull(5) != null) "Personal choice" else "",
                strMeasure7 = if (ingredientsList.getOrNull(6) != null) "Personal choice" else "",
                strMeasure8 = if (ingredientsList.getOrNull(7) != null) "Personal choice" else "",
                strMeasure9 = if (ingredientsList.getOrNull(8) != null) "Personal choice" else "",
                strMeasure10 = if (ingredientsList.getOrNull(9) != null) "Personal choice" else ""
            )
        }
    }

}