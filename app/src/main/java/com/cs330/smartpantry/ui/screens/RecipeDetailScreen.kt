package com.cs330.smartpantry.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

@Composable
fun RecipeDetailScreen(
    mealId: String, viewModel: RecipeViewModel = hiltViewModel()
) {
    val meal by viewModel.selectedRecipe.collectAsState()

    LaunchedEffect (mealId){
        viewModel.getRecipeDetails(mealId)
    }
    meal?.let {
        Column (
            modifier = Modifier.verticalScroll(rememberScrollState())
        ){
            AsyncImage(model = it.strMealThumb, contentDescription = null, modifier = Modifier.fillMaxWidth())
            Text(it.strMeal, style = MaterialTheme.typography.headlineLarge, modifier = Modifier.padding(8.dp))
            Text(it.strInstructions ?: "No instruction", modifier = Modifier.padding(16.dp))
        }
    }
}