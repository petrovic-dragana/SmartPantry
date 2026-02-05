package com.cs330.smartpantry.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

@Composable
fun RecipeScreen(
    viewModel: RecipeViewModel = hiltViewModel()
) {
    val recipes by viewModel.recipes.collectAsState()
    var query by remember { mutableStateOf("") }

    Column (modifier = Modifier.fillMaxSize().padding(16.dp)){
        OutlinedTextField(value = query, onValueChange = {query = it}, label = { Text("Traži recepte po namirnici (npr. chicken)") },
            modifier = Modifier.fillMaxWidth())
        Button(onClick = {viewModel.searchByIngredient(query)}) {
            Text("Traži")
        }

        LazyColumn {
            items(recipes){meal ->
                Card(modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth()) {
                    Row (verticalAlignment = Alignment.CenterVertically){
                        AsyncImage(
                            model = meal.strMealThumb,
                            contentDescription = meal.strMeal,
                            modifier = Modifier.size(100.dp)
                        )
                        Text(meal.strMeal, modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }
}