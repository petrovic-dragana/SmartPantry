package com.cs330.smartpantry.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.cs330.smartpantry.model.MealDto
import com.cs330.smartpantry.model.Recipe

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    onRecipeClick: (String) -> Unit,
    viewModel: RecipeViewModel = hiltViewModel()
) {
    val pantryRecipes by viewModel.pantryMatchRecipes.collectAsStateWithLifecycle()
    val categoryRecipes by viewModel.categoryRecipes.collectAsStateWithLifecycle()
    val selectedCat by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val favoriteIds by viewModel.favoriteRecipesIds.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // NASLOV
        item {
            Text("Discover Recipes", style = MaterialTheme.typography.headlineLarge)
        }
        //1.PANTRY MATCH
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Use What You Have", style = MaterialTheme.typography.titleMedium)
                    //ovde dodati see all screen
                    TextButton(onClick = { /* Pogledaj sve */ }) {
                        Text("See All", color = Color(0xFFE57373))
                    }
                }

                if (pantryRecipes.isEmpty()) {
                    Text("Add items to your pantry to see magic!",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray)
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(pantryRecipes) { recipe ->
                            SmallRecipeCard(recipe, onRecipeClick)
                        }
                    }
                }
            }
        }
        // 2. KATEGORIJE
        item {
            CategorySection(
                selectedCategory = selectedCat,
                onCategorySelected = { categoryName ->
                    viewModel.selectCategory(categoryName)}
            )
        }

        // 3. POPULARNA JELA
        item {
            Text("Popular in $selectedCat", style = MaterialTheme.typography.titleMedium)
        }

        items(categoryRecipes) { recipe ->
            val isFav = favoriteIds.contains(recipe.id)
            RecipeCard(
                name = recipe.title,
                duration = "Check instructions",
                imageUrl = recipe.imageUrl,
                isFavorite = isFav,
                onFavoriteToggle = {
                    viewModel.toggleFavorite(MealDto(
                        idMeal = recipe.id,
                        strMeal = recipe.title,
                        strMealThumb = recipe.imageUrl,
                        strInstructions = recipe.summary
                    ))
                },
                onClick = {onRecipeClick(recipe.id)}
            )
        }
    }
}

@Composable
fun SmallRecipeCard(
    recipe: Recipe,
    onClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(200.dp)
            .clickable { onClick(recipe.id) },
        shape = RoundedCornerShape(12.dp)
    ) {
        Box {
            AsyncImage(
                model = recipe.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Surface(
                color = Color.Black.copy(alpha = 0.6f),
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
            ) {
                Text(
                    text = recipe.title,
                    color = Color.White,
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1
                )
            }
        }
    }
}
val mainCategories = listOf(
    "Seafood" to "🐟",
    "Chicken" to "🍗",
    "Dessert" to "🍰",
    "Pasta" to "🍝",
    "Pizza" to "🍕",
    "Vegetarian" to "🥗"
)
@Composable
fun CategorySection(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    Column {
        Text("Categories", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(mainCategories) { (name, icon) ->
                val isSelected = name == selectedCategory

                Surface(
                    modifier = Modifier.clickable { onCategorySelected(name) },
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) Color(0xFFE57373) else Color(0xFFF5F5F5),
                    shadowElevation = if (isSelected) 4.dp else 0.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(icon, modifier = Modifier.padding(end = 8.dp))
                        Text(
                            text = name,
                            color = if (isSelected) Color.White else Color.Black,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecipeCard(
    name: String,
    duration: String,
    imageUrl: String,
    isFavorite: Boolean = false,
    onFavoriteToggle: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .clickable{onClick()}
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Surface (
                color = Color.Black.copy(alpha = 0.5f),
                shape = RoundedCornerShape( 16.dp),
                modifier = Modifier.align(Alignment.TopEnd).padding(12.dp)

            ){IconButton(onClick = onFavoriteToggle) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = if (isFavorite) Color.Red else Color.White
                )
            }  }

            Surface(
                color = Color.Black.copy(alpha = 0.5f),
                shape = RoundedCornerShape(bottomStart = 16.dp),
                modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(name, color = Color.White, style = MaterialTheme.typography.titleLarge)
                    Text(duration, color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
