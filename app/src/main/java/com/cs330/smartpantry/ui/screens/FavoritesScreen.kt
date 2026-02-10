package com.cs330.smartpantry.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.cs330.smartpantry.model.Recipe
import com.cs330.smartpantry.model.toBealDto
import java.nio.file.WatchEvent

@Composable
fun FavoritesScreen(
    viewModel: RecipeViewModel = hiltViewModel(),
    onRecipeClick: (String) -> Unit
)
{
    val favorites by viewModel.favoriteRecipes.collectAsStateWithLifecycle()

    Column (modifier = Modifier
        .fillMaxSize()
        .padding(16.dp))
    {
        Text(
            text = "My Favorite Recipes",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )
        if (favorites.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                Text("No favorites yet. Start liking some recipes!")
            }
        }
        else{
            LazyColumn {
                items(favorites, key = { it.id }) { recipe -> // Dodaj key radi boljih performansi
                    FavoritesRecipeItem(
                        recipe = recipe,
                        onRecipeClick = {
                            println("Is it clicked ${recipe.id}")
                            onRecipeClick(recipe.id)
                        },
                        onRemove = { viewModel.toggleFavorite(recipe.toBealDto()) }
                    )
                }
            }
        }
    }
}
@Composable
fun FavoritesRecipeItem(
    recipe: Recipe,
    onRecipeClick: () -> Unit,
    onRemove: () -> Unit
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable{onRecipeClick()},
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically
        ){
            AsyncImage(
                model = recipe.imageUrl,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                text = recipe.title,
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f),
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2
            )
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Remove from favorites",
                    tint = Color.Red
                )
            }
        }
    }
}