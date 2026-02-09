package com.cs330.smartpantry.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.cs330.smartpantry.data.repository.PantryRepository
import com.cs330.smartpantry.model.Recipe

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    onRecipeClick: (String) -> Unit,
    pantryRecipes: List<Recipe> = emptyList()
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // NASLOV
        item {
            Text("Discover Recipes", style = MaterialTheme.typography.headlineLarge)
        }
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Use What You Have", style = MaterialTheme.typography.titleMedium)
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
                            // Manja kartica za horizontalni skrol
                            SmallRecipeCard(recipe, onRecipeClick)
                        }
                    }
                }
            }
        }
        // 1. KATEGORIJE (Tvoja prva slika - Grid stil)
        item {
            Text("Categories", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Primer nekoliko kategorija sa tvoje slike
                CategoryChip("Pizza", Color(0xFFE57373))
                CategoryChip("Vegetables", Color.White)
                CategoryChip("Fruit", Color(0xFFE57373))
                CategoryChip("Noodles", Color.White)
                // ... dodaj ostale
            }
        }

        // 2. POPULARNA JELA (Tvoja druga slika - Kartice u koloni)
        item {
            Text("Popular Choices", style = MaterialTheme.typography.titleMedium)
        }

        items(listOf("Lasagna", "Pasta", "Salad")) { recipeName ->
            RecipeCard(
                name = if (recipeName == "Lasagna") "100-Layer Lasagna" else recipeName,
                duration = "Under 60 minutes",
                imageUrl = "https://www.themealdb.com/images/media/meals/xr0n4r1575883077.jpg" // Primer slike
            )
        }
    }
}

@Composable
fun SmallRecipeCard(recipe: Recipe, onClick: (String) -> Unit) {
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

@Composable
fun CategoryChip(name: String, backgroundColor: Color) {
    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 2.dp,
        modifier = Modifier.clickable { /* Filter */ }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ovde možeš dodati Icon(...) pre teksta kao na slici
            Text(
                text = name,
                color = if (backgroundColor == Color.White) Color.Black else Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun RecipeCard(name: String, duration: String, imageUrl: String) {
    Card(
        modifier = Modifier.fillMaxWidth().height(250.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Gradijent i tekst preko slike
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(name, color = Color.White, style = MaterialTheme.typography.titleLarge)
                Text(duration, color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
@Composable
fun RecipeCard(
    name: String,
    duration: String,
    imageUrl: String,
    isFavorite: Boolean = false, // Dodajemo stanje
    onFavoriteClick: () -> Unit = {} // Akciju za klik
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(vertical = 4.dp), // Mala margina
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // SLIKA RECEPTA
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // DUGME SRCE (Gornji desni ugao)
            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) Color.Red else Color.White
                )
            }

            // TEKST PREKO SLIKE (Donji deo sa gradijentom)
            Surface(
                color = Color.Black.copy(alpha = 0.5f), // Poluprovidna pozadina za tekst
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(name, color = Color.White, style = MaterialTheme.typography.titleLarge)
                    Text(duration, color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}