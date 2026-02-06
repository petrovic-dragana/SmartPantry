package com.cs330.smartpantry.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage


val quickIngredients = listOf(
    // Povrće
    "Tomato", "Potato", "Onion", "Garlic", "Carrot", "Broccoli", "Cucumber",
    // Voće
    "Apple", "Banana", "Lemon", "Orange", "Strawberry",
    // Proteini & Mlečno
    "Chicken", "Beef", "Eggs", "Milk", "Cheese", "Butter",
    // Ostalo
    "Rice", "Pasta", "Flour", "Sugar", "Oil"
)

@Composable
fun PantryScreen(viewModel: PantryViewModel = hiltViewModel()){
    val ingredients by viewModel.ingredients.collectAsStateWithLifecycle()
    var selectedIngredient by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Smart Pantry Market", style = MaterialTheme.typography.headlineMedium)
        Text("Tap an ingredient to quick add", style = MaterialTheme.typography.bodySmall)

        Spacer(modifier = Modifier.height(8.dp))
        // 1. HORIZONTALNA LISTA SA SLIČICAMA (MARKET)
        androidx.compose.foundation.lazy.LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            items(quickIngredients) { name ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable() {
                        selectedIngredient = name
                    }
                ) {
                    androidx.compose.material3.Card(
                        shape = androidx.compose.foundation.shape.CircleShape,
                        modifier = Modifier.size(65.dp)
                    ) {
                        AsyncImage(
                            model = "https://www.themealdb.com/images/ingredients/$name-Small.png",
                            contentDescription = name,
                            modifier = Modifier.fillMaxSize().padding(8.dp)
                        )
                    }
                    Text(name, style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Current Stock", style = MaterialTheme.typography.titleLarge)

        // 2. LISTA NAMIRNICA KOJE SU VEĆ U OSTAVI
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(ingredients) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Mala sličica i u listi za bolji izgled
                        AsyncImage(
                            model = "https://www.themealdb.com/images/ingredients/${item.name}-Small.png",
                            contentDescription = null,
                            modifier = Modifier.size(40.dp).padding(end = 8.dp)
                        )
                        Text("${item.name}: ${item.quantity} ${item.unit}")
                    }
                    IconButton(onClick = { viewModel.deleteIngredient(item) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                }
                androidx.compose.material3.HorizontalDivider()
            }
        }
    }// 3. PRIKAZ DIALOGA KADA SE KLIKNE NA SLIČICU
    selectedIngredient?.let { name ->
        QuickAddDialog(
            ingredientName = name,
            onDismiss = { selectedIngredient = null },
            onConfirm = { quantityValue, unitValue ->
                // Ovde prosleđujemo podatke u ViewModel
                viewModel.addIngredient(name, quantityValue.toDouble(), unitValue)
                selectedIngredient = null
            }
        )
    }
}

@Composable
fun QuickAddDialog(
    ingredientName: String,
    onDismiss: () -> Unit,
    onConfirm: (Int, String) -> Unit
) {
    var quantity by remember { mutableStateOf(1) }
    val units = listOf("pcs", "kg", "g", "l", "ml")
    var selectedUnit by remember { mutableStateOf(units[0]) }

    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = "https://www.themealdb.com/images/ingredients/$ingredientName.png",
                    contentDescription = null,
                    modifier = Modifier.size(100.dp)
                )
                Text("Add $ingredientName", style = MaterialTheme.typography.headlineSmall)
            }
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("Select Quantity", style = MaterialTheme.typography.labelLarge)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    // Strelica DOLE
                    IconButton(onClick = {
                        if (quantity > 0) quantity--
                    }) {
                        Icon(painter = painterResource(id = android.R.drawable.arrow_down_float), contentDescription = "Down")
                    }

                    // Prikaz broja
                    Text(
                        text = quantity.toString(),
                        style = MaterialTheme.typography.displaySmall,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )

                    // Strelica GORE
                    IconButton(onClick = {
                        if (quantity < 50) quantity++
                    }) {
                        Icon(painter = painterResource(id = android.R.drawable.arrow_up_float), contentDescription = "Up")
                    }
                }

                if (showError) {
                    Text(
                        text = "Quantity must be greater than 0!",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Izbor jedinica
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    units.forEach { unit ->
                        FilterChip(
                            selected = (selectedUnit == unit),
                            onClick = { selectedUnit = unit },
                            label = { Text(unit) },
                            modifier = Modifier.padding(horizontal = 2.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (quantity > 0) {
                    onConfirm(quantity, selectedUnit)
                    showError = false
                } else {
                    showError = true
                }
            }) { Text("Confirm") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}