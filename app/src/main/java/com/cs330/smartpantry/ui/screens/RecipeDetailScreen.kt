package com.cs330.smartpantry.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

@Composable
fun RecipeDetailScreen(
    mealId: String, viewModel: RecipeViewModel = hiltViewModel()
) {
    val meal by viewModel.selectedRecipe.collectAsState()
    val isFav by viewModel.isCurrentRecipeFavorite(mealId).collectAsState(initial = false)

    LaunchedEffect (mealId){
        viewModel.getRecipeDetails(mealId)
    }
    meal?.let {
        Scaffold (
            floatingActionButton = {
                FloatingActionButton(onClick = {meal?.let { viewModel.toggleFavorite(it) } }) {
                    Icon(Icons.Default.Favorite, contentDescription = "Add to Favorites", tint = Color.Red)}
            }
        ) { padding ->
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                AsyncImage(
                    model = it.strMealThumb,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    it.strMeal,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(8.dp)
                )
                Text(it.strInstructions ?: "No instruction", modifier = Modifier.padding(16.dp))
//            Icon(
//                imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
//                tint = if (isFav) Color.Red else Color.Gray,
//                contentDescription = null
//            )
            }

        }
    }
}
