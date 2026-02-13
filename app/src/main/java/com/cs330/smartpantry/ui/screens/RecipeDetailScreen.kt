package com.cs330.smartpantry.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.cs330.smartpantry.ui.viewmodel.RecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    mealId: String,
    viewModel: RecipeViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val meal by viewModel.selectedRecipe.collectAsState()

    val isFav by viewModel.isCurrentRecipeFavorite(mealId).collectAsState(initial = false)

    LaunchedEffect (mealId){
        viewModel.getRecipeDetails(mealId)
    }
    meal?.let { recipe ->
        Scaffold (
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { viewModel.toggleFavorite(recipe) },
                    containerColor = Color.Black.copy(alpha = 0.5f)) {
                    Icon(
                        imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFav) Color.Red else Color.White
                    )
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Box(modifier = Modifier.height(300.dp)) {
                    AsyncImage(
                        model = recipe.strMealThumb,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    )

                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.padding(top = 40.dp, start = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
                Column(modifier = Modifier.padding(16.dp)){
                    Text(
                        recipe.strMeal,
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.padding(8.dp)
                    )

                    Text(
                        "Ingredients",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF1F731B),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    val ingredients = recipe.getIngredientsWithMeasures()

                    if (ingredients.isEmpty()) {
                        Text("No ingredients listed.", modifier = Modifier.padding(horizontal = 24.dp))
                    } else {
                        ingredients.forEach { ingredient ->
                            Text(
                                "• $ingredient",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "Instructions",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF1F731B),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Text(recipe.strInstructions ?: "No instruction",
                        modifier = Modifier.padding(16.dp))

                }

            }

        }
    }
}
