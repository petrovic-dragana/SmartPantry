package com.cs330.smartpantry.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.cs330.smartpantry.model.MealDto
import com.cs330.smartpantry.ui.viewmodel.RecipeViewModel

@Composable
fun RecipeScreen(
    viewModel: RecipeViewModel = hiltViewModel(),
    onRecipeClick: (String) -> Unit
) {
    val recipes by viewModel.recipes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val favoriteIds by viewModel.favoriteRecipesIds.collectAsState()

    var query by remember { mutableStateOf("") }
    var searchByName by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Search Recipes",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 1. Polje za unos sa dugmetom
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                viewModel.onQueryChange(it)
            },
            label = { Text(if (searchByName) "Search by name (e.g. pizza)" else "Search by ingredient (e.g. egg)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            trailingIcon = {
                IconButton(onClick = { viewModel.searchRecipes(query, searchByName) }) {
                    Icon(Icons.Default.Search, contentDescription = "Search Now")
                }
            }
        )

        // 2. Filter Chips odmah ispod pretrage
        Row(
            modifier = Modifier.padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = searchByName,
                onClick = { searchByName = true },
                label = { Text("Name") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF1F731B),
                    selectedLabelColor = Color.White,
                    selectedLeadingIconColor = Color.White
                ),
                leadingIcon = if (searchByName) {

                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                } else null
            )
            FilterChip(
                selected = !searchByName,
                onClick = { searchByName = false },
                label = { Text("Ingredient") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF1F731B),
                    selectedLeadingIconColor = Color.White,
                    selectedLabelColor = Color.White
                ),
                leadingIcon = if (!searchByName) {
                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                } else null
            )
        }

        // 3. Loading indikator (samo dok se učitava)
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize().padding(bottom = 8.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }


        // 4. Prikaz rezultata
        Box(modifier = Modifier.weight(1f)) {
            if (recipes.isEmpty() && !isLoading) {
                Text(
                    "No recipes found. Try another search!",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Gray
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(alpha = if (isLoading) 0.4f else 1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(recipes) { meal ->
                        val isFav = favoriteIds.contains(meal.idMeal)
                        RecipeListItem(
                            meal = meal,
                            onRecipeClick = onRecipeClick,
                            isFavorite = isFav,
                            onFavoriteToggle = {
                                viewModel.toggleFavorite(meal)
                            })
                    }
                }
            }
        }
    }
}
@Composable
fun RecipeListItem(
    meal: MealDto,
    isFavorite: Boolean,
    onRecipeClick: (String) -> Unit ,
    onFavoriteToggle: () -> Unit)
{
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onRecipeClick(meal.idMeal) },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(end = 16.dp)
            ) {
            AsyncImage(
                model = meal.strMealThumb,
                contentDescription = meal.strMeal,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(100.dp)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                Text(meal.strMeal, style = MaterialTheme.typography.titleMedium)
                Text("View details", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            IconButton(onClick = onFavoriteToggle) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = if (isFavorite) Color.Red else Color.Gray
                )
            }
        }
    }
}
