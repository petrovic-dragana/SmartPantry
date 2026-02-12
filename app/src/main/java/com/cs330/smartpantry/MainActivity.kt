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
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cs330.smartpantry.ui.screens.AboutScreen
import com.cs330.smartpantry.ui.screens.FavoritesScreen
import com.cs330.smartpantry.ui.screens.HomeScreen
import com.cs330.smartpantry.ui.screens.MyRecipesScreen
import com.cs330.smartpantry.ui.screens.PantryScreen
import com.cs330.smartpantry.ui.screens.RecipeDetailScreen
import com.cs330.smartpantry.ui.screens.RecipeScreen
import com.cs330.smartpantry.ui.screens.RecipeViewModel
import com.cs330.smartpantry.ui.screens.Screen
import com.cs330.smartpantry.ui.theme.SmartPantryTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartPantryTheme {
                Surface (
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ){
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

                val items = listOf(
                    Screen.Home,
                    Screen.Pantry,
                    Screen.Recipes,
                    Screen.Favorite,
                    Screen.MyRecipes,
                    Screen.About)

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
        NavHost(navController, startDestination = Screen.Home.route, Modifier.padding(innerPadding)) {
            composable(Screen.Home.route){
                HomeScreen(
                    onRecipeClick = { id ->
                        navController.navigate(Screen.Details.createRoute(id))
                    }
                )
            }
            composable(Screen.Pantry.route) { PantryScreen() }

            composable(Screen.Recipes.route) {
                RecipeScreen(onRecipeClick = { mealId ->
                    navController.navigate(Screen.Details.createRoute(mealId))
                })
            }
            composable(Screen.Favorite.route) {
                FavoritesScreen(onRecipeClick = { mealId ->
                        navController.navigate(Screen.Details.createRoute(mealId))
                    }
                )
            }
            composable(Screen.MyRecipes.route){
                MyRecipesScreen(navController = navController)
            }
            composable(
                route = Screen.Details.route,
                arguments = listOf(navArgument("mealId") { type = NavType.StringType })
            ) { backStackEntry ->
                val mealId = backStackEntry.arguments?.getString("mealId") ?: ""
                RecipeDetailScreen(
                    mealId = mealId,
                    onBackClick = {
                        navController.popBackStack()
                    })
            }
            composable(Screen.About.route) {
                AboutScreen()
            }
        }
    }
}