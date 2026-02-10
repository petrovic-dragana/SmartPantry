package com.cs330.smartpantry.ui.screens

import android.graphics.drawable.Icon
import android.icu.text.CaseMap.Title
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import okhttp3.Route

sealed class Screen (val route: String, val title: String, val icon: ImageVector){

    object Home: Screen("home", "Home", Icons.Default.Home)
    object Pantry : Screen("pantry", "Pantry", Icons.Default.List)
    object Recipes : Screen("recipes", "Recipes", Icons.Default.Search)
    object Favorite : Screen ("favorites", "Favorite", Icons.Default.Favorite)
    object Details : Screen("details/{mealId}", "Details", Icons.Default.Info){
        fun createRoute(mealId: String ) = "details/$mealId"
    }
    object About : Screen("about", "About", Icons.Default.Info)
}