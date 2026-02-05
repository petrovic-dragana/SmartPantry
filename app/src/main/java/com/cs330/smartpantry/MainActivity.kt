package com.cs330.smartpantry

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cs330.smartpantry.ui.screens.PantryScreen
import com.cs330.smartpantry.ui.screens.RecipeDetailScreen
import com.cs330.smartpantry.ui.screens.RecipeScreen
import com.cs330.smartpantry.ui.screens.Screen
import com.cs330.smartpantry.ui.theme.SmartPantryTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartPantryTheme {
                Surface (
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ){
                    //PantryScreen()
                   // RecipeScreen()
                    MainScreen()
                }
            }
        }
    }
}
@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                val items = listOf(Screen.Pantry, Screen.Recipes)//, Screen.Favorites)

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = Screen.Pantry.route, Modifier.padding(innerPadding)) {
            composable(Screen.Pantry.route) { PantryScreen() }
            composable(Screen.Recipes.route) {
                // Prosleđujemo navController da bismo mogli da kliknemo na recept
                RecipeScreen(onRecipeClick = { mealId ->
                    navController.navigate(Screen.Details.createRoute(mealId))
                })
            }
            // composable(Screen.Favorites.route) { /* Ovde ćemo kasnije FavoritesScreen */ }
            composable(
                route = Screen.Details.route,
                arguments = listOf(navArgument("mealId") { type = NavType.StringType })
            ) { backStackEntry ->
                val mealId = backStackEntry.arguments?.getString("mealId") ?: ""
                RecipeDetailScreen(mealId = mealId)
            }
        }
    }
}