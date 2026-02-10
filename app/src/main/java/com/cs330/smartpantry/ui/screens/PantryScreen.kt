package com.cs330.smartpantry.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.cs330.smartpantry.model.Ingredient

// 1. MAPE NAMIRNICA (Van Composable-a)
val categories = mapOf(
    "Vegetables" to listOf("Tomato", "Potato", "Onion", "Garlic", "Carrot", "Broccoli", "Cucumber"),
    "Fruit" to listOf("Apple", "Banana", "Lemon", "Orange", "Strawberry"),
    "Meat" to listOf("Chicken", "Beef", "Pork", "Bacon"),
    "Dairy" to listOf("Milk", "Cheese", "Butter", "Yogurt"),
    "Pantry" to listOf("Rice", "Pasta", "Flour", "Sugar", "Oil")
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PantryScreen(viewModel: PantryViewModel = hiltViewModel()) {
    val ingredients by viewModel.ingredients.collectAsStateWithLifecycle()
    var selectedCategory by remember { mutableStateOf("Vegetables") }
    var selectedIngredient by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var isMarketExpanded by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<Ingredient?>(null) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Smart Pantry Market", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // SEARCH BAR
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search market...") },
            trailingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Button(
                onClick = { isMarketExpanded = !isMarketExpanded },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isMarketExpanded) Color.Gray else Color(0xFFE57373)
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(if (isMarketExpanded) "Close Market" else "Add Items +")
            }
        }

        // ANIMIRANI DEO - Prikazuje se samo kad je isMarketExpanded true
        androidx.compose.animation.AnimatedVisibility(
            visible = isMarketExpanded,
            enter = androidx.compose.animation.expandVertically(),
            exit = androidx.compose.animation.shrinkVertically()
        ) {
            Column {
                Spacer(modifier = Modifier.height(16.dp))

                // KATEGORIJE
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.keys.forEach { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFE57373),
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // MREŽA NAMIRNICA
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val currentList = categories[selectedCategory] ?: emptyList()
                    val filteredList = currentList.filter { it.contains(searchQuery, ignoreCase = true) }

                    filteredList.forEach { name ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(70.dp).clickable { selectedIngredient = name }
                        ) {
                            Card(
                                shape = CircleShape,
                                modifier = Modifier.size(65.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)), // Svetlo siva pozadina
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                AsyncImage(
                                    model = "https://www.themealdb.com/images/ingredients/$name.png",
                                    contentDescription = name,
                                    contentScale = androidx.compose.ui.layout.ContentScale.Fit,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp),
                                    error = painterResource(id = android.R.drawable.ic_menu_help), // Upitnik ako nema slike
                                    placeholder = painterResource(id = android.R.drawable.ic_menu_gallery) // Galerija dok učitava
                                )
                            }
                            Text(name, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
                Divider(modifier = Modifier.padding(vertical = 16.dp))
            }
        }
        //LISTA TRENUTNOG STANJA
        Spacer(modifier = Modifier.height(16.dp))
        Text("My Current Stock", style = MaterialTheme.typography.titleLarge)

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(ingredients) { item ->
                ListItem(
                    modifier = Modifier.clickable{itemToEdit = item},
                    headlineContent = { Text(item.name) },
                    supportingContent = { Text("${item.quantity} ${item.unit}") },
                    leadingContent = {
                        AsyncImage(
                            model = "https://www.themealdb.com/images/ingredients/${item.name}-Small.png",
                            contentDescription = null,
                            modifier = Modifier.size(40.dp)
                        )
                    },
                    trailingContent = {
                        IconButton(onClick = { viewModel.deleteIngredient(item) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.LightGray)
                        }
                    }
                )
                HorizontalDivider()
            }
        }
    }

    // DIALOG ZA UNOS
    selectedIngredient?.let { name ->
        QuickAddDialog(
            ingredientName = name,
            themeColor = Color(0xFFE57373),
            onDismiss = { selectedIngredient = null },
            onConfirm = { qty, unit ->
                viewModel.addIngredient(name, qty.toDouble(), unit)
                selectedIngredient = null
            }
        )
    }
    //DIALOG ZA IZMENU
    itemToEdit?.let { item ->
        QuickAddDialog(
            ingredientName = item.name,
            initialQuantity = item.quantity.toInt(),
            initialUnit = item.unit,
            themeColor = Color(0xFFE57373),
            onDismiss = { itemToEdit = null },
            onConfirm = { qty, unit ->
                viewModel.updateIngredient(item, qty.toDouble(), unit)
                itemToEdit = null
            }
        )

    }
}

@Composable
fun QuickAddDialog(
    ingredientName: String,
    initialQuantity: Int = 1,
    initialUnit:String = "pcs",
    themeColor: Color = MaterialTheme.colorScheme.primary,
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
                    IconButton(onClick = { if (quantity > 0) quantity-- }) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.arrow_down_float),
                            contentDescription = "Down",
                            tint = themeColor
                        )
                    }
                    Text(
                        text = quantity.toString(),
                        style = MaterialTheme.typography.displaySmall,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    IconButton(onClick = { if (quantity < 50) quantity++ }) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.arrow_up_float),
                            contentDescription = "Up",
                            tint = themeColor
                        )
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
                            modifier = Modifier.padding(horizontal = 2.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = themeColor,
                                selectedLabelColor = Color.White
                            )
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
            }, colors = ButtonDefaults.buttonColors(containerColor = themeColor)
                ) { Text("Confirm") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = themeColor) }
        }
    )
}