package com.cs330.smartpantry.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun PantryScreen(viewModel: PantryViewModel = hiltViewModel()){
    val ingredients by viewModel.ingredients.collectAsStateWithLifecycle()
    var name by remember { mutableStateOf("") }
    var qty by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Moja Ostava (Smart Pantry)", style = MaterialTheme.typography.headlineMedium)

        //Forma za unos
        OutlinedTextField(value = name, onValueChange = {name = it}, label = { Text("Namirnica") })
        OutlinedTextField(value = qty, onValueChange = {qty = it}, label = { Text("Količina") })

        Button(onClick = {
            if (name.isNotBlank() && qty.isNotBlank()) {
                viewModel.addIngredient(name, qty.toDoubleOrNull() ?: 0.0, "kom")
                name = ""; qty = ""

            }
        }) { Text("Dodaj u ostavu") }

        Spacer(modifier = Modifier.height(16.dp))

        //Lista namirnica
        LazyColumn {
            items(ingredients){ item ->
               Row (modifier = Modifier.fillMaxWidth().padding(8.dp),
                   horizontalArrangement = Arrangement.SpaceBetween) {
                   Text("${item.name}: ${item.quantity} ${item.unit}")
                   IconButton(onClick = { viewModel.deleteIngredient(item) }) {
                       Icon(Icons.Default.Delete, contentDescription = "Obriši")
                   }
               }
            }
        }
    }
}