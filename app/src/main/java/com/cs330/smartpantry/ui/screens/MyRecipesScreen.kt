package com.cs330.smartpantry.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.cs330.smartpantry.model.CustomRecipe
import com.cs330.smartpantry.ui.navigation.Screen
import com.cs330.smartpantry.ui.viewmodel.CustomRecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyRecipesScreen(
    navController: NavController,
    viewModel: CustomRecipeViewModel = hiltViewModel()
){
    val recipes by viewModel.recipes.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var recipeToEdit by remember { mutableStateOf<CustomRecipe?>(null) }
    var recipeToShowDetails by remember { mutableStateOf<CustomRecipe?>(null) }

    Scaffold (
        floatingActionButton = {
            FloatingActionButton(
                onClick = {showAddDialog = true},
                containerColor = Color(0xFF1F731B),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Recipe")
            }
        }
    ){padding ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ){
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = { navController.popBackStack() }
                ) {
                    Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back", tint = Color.Gray)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "My Custom Recipes",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Manage your private recipes",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (recipes.isEmpty()) {
                Box (modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                    Text("No custom recipes found on server.")
                }
            }else{
                LazyColumn (verticalArrangement = Arrangement.spacedBy(12.dp)){
                    items(recipes){recipe ->
                        RecipeItemCard(
                            recipe = recipe,
                            onEdit = { recipeToEdit = recipe },
                            onDelete = { recipe.id?.let { viewModel.deleteRecipe(it) } },
                            onViewDetails = {
                                navController.navigate(Screen.Details.createRoute(recipe.id ?: ""))                            }
                        )
                    }
                }
            }

        }
    }
    if (showAddDialog) {
        RecipeFormDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, ing, instr, imgUrl ->
                viewModel.addRecipe(title, ing, instr, imgUrl)
                showAddDialog = false
            }
        )
    }
    recipeToShowDetails?.let { recipe ->
        RecipeDetailsDialog(recipe = recipe, onDismiss = { recipeToShowDetails = null })
    }

    // DIJALOG ZA IZMENU (UPDATE)
    recipeToEdit?.let { recipe ->
        RecipeFormDialog(
            initialRecipe = recipe,
            onDismiss = { recipeToEdit = null },
            onConfirm = { title, ing, instr, imgUrl ->
                recipe.id?.let { viewModel.updateRecipe(it, title, ing, instr, imgUrl) }
                recipeToEdit = null
            }
        )
    }
}

@Composable
fun RecipeItemCard(recipe: CustomRecipe, onEdit: () -> Unit, onDelete: () -> Unit, onViewDetails: () -> Unit) {
    val ingredientList = recipe.ingredients.split(",").map { it.trim() }
    Card(
        modifier = Modifier.fillMaxWidth().clickable{onViewDetails()},
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically)  {
            AsyncImage(
                model = recipe.imageUrl ?: "https://via.placeholder.com/150",
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier
                .weight(1f)
                .padding(12.dp)) {
                Text(text = recipe.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(4.dp))

                ingredientList.take(2).forEach { ingredient ->
                    Text(text = "• $ingredient", style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                }
                if (ingredientList.size > 2) {
                    Text(text = "...", style = MaterialTheme.typography.bodySmall)
                }
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Gray)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFE57373))
                }
            }
        }
    }
}

@Composable
fun RecipeFormDialog(
    initialRecipe: CustomRecipe? = null,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String?) -> Unit
) {
    var title by remember { mutableStateOf(initialRecipe?.title ?: "") }
    var ingredients by remember { mutableStateOf(initialRecipe?.ingredients ?: "") }
    var instructions by remember { mutableStateOf(initialRecipe?.instructions ?: "") }
    var imageUri by remember { mutableStateOf<Uri?>(initialRecipe?.imageUrl?.let { Uri.parse(it) }) }

    var showError by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ){ uri: Uri? ->
        uri?.let {
            try {
                val contentResolver = context.contentResolver
                val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
                contentResolver.takePersistableUriPermission(it, takeFlags)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            imageUri = it
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialRecipe == null) "New Recipe" else "Edit Recipe") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clickable { galleryLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(imageUri ?: "https://via.placeholder.com/150")
                            .crossfade(true)
                            .build(),
                        contentDescription = "Recipe Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    if (imageUri == null) {
                        Text("Click to add a photo", color = Color.Gray, fontWeight = FontWeight.Bold)
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; showError = false },
                    label = { Text("Title") },
                    isError = showError && title.isBlank(),
                    modifier = Modifier.fillMaxWidth())

                OutlinedTextField(
                    value = ingredients,
                    onValueChange = { ingredients = it; showError = false },
                    label = { Text("Ingredients") },
                    isError = showError && ingredients.isBlank(),
                    modifier = Modifier.fillMaxWidth())

                OutlinedTextField(
                    value = instructions,
                    onValueChange = { instructions = it; showError = false },
                    label = { Text("Instruction") },
                    isError = showError && instructions.isBlank(),
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                if (showError){
                    Text(
                        text = "All fields must be filled!",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && ingredients.isNotBlank() && instructions.isNotBlank()) {
                    onConfirm(title, ingredients, instructions, imageUri?.toString())
                } else {
                    showError = true // Prikazujemo grešku ako nešto fali
                }
                          },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F731B))
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
@Composable
fun RecipeDetailsDialog(recipe: CustomRecipe, onDismiss: () -> Unit) {
    val ingredientList = recipe.ingredients.split(",").map { it.trim() }.filter { it.isNotEmpty() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = recipe.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Ingredients:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

                // BULLET POINTS za sve sastojke
                ingredientList.forEach { ingredient ->
                    Text(text = "• $ingredient", style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Instructions:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(text = recipe.instructions, style = MaterialTheme.typography.bodyMedium)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close", color = Color(0xFF1F731B)) }
        }
    )
}