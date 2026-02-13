package com.cs330.smartpantry.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.cs330.smartpantry.ui.navigation.Screen
import com.cs330.smartpantry.ui.viewmodel.CustomRecipeViewModel
import com.cs330.smartpantry.ui.viewmodel.RecipeViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    customRecipeViewModel: CustomRecipeViewModel = hiltViewModel(),
    recipeViewModel: RecipeViewModel = hiltViewModel(),

    ) {
    val myRecipes by customRecipeViewModel.recipes.collectAsStateWithLifecycle()
    val favoriteIds by recipeViewModel.favoriteRecipesIds.collectAsState()

    val myRecipesCount = myRecipes.size
    val  favoritesCount = favoriteIds.size

    var showMenu by remember { mutableStateOf(false )}
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(modifier = Modifier.fillMaxWidth()) {
            IconButton(
                onClick = { navController.navigate(Screen.About.route)},
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(Icons.Default.Info, contentDescription = "About", tint = Color.Gray)
            }
        }

        // 1. CENTRALNA SLIKA (AVATAR)
        Surface(
            modifier = Modifier.size(150.dp),
            shape = CircleShape,
            color = Color(0xFFF5F5F5),
            shadowElevation = 4.dp
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.padding(35.dp),
                tint = Color(0xFF77B23F)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // IME I LOKACIJA
        Text(
            text = "Korisnik Korisniković",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Smart Pantry Enthusiast",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(40.dp))

        // 2. STATISTIKA (Brojevi recepata)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Broj mojih recepata
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = myRecipesCount.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F731B)
                )
                Text(
                    text = "My Recipes",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Box(
                modifier = Modifier
                    .height(40.dp)
                    .width(1.dp)
                    .background(Color.LightGray)
            )

            // Broj omiljenih recepata
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = favoritesCount.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F731B)
                )
                Text(
                    text = "Favorites",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(50.dp))

        // 3. BRZE AKCIJE (Tri dugmeta u nizu)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            QuickActionButton(
                icon = Icons.Default.EditNote,
                label = "My Recipes",
                onClick = {navController.navigate("myrecipe")})
            QuickActionButton(
                icon = Icons.Default.Favorite,
                label = "Favorite",
                onClick = {navController.navigate("favorites")}
            )
           // QuickActionButton(icon = Icons.Default.Share, label = "Share")
        }
    }
}

@Composable
fun QuickActionButton(icon: ImageVector, label: String, onClick: () -> Unit ){
    Surface(
        modifier = Modifier
            .size(70.dp)
            .clickable{onClick()},
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFF9F9F9),
        shadowElevation = 2.dp
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = if (label == "Favorite") Color(0xFFE57373) else Color.DarkGray, modifier = Modifier.size(24.dp))
        }
    }
}