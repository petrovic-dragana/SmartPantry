package com.cs330.smartpantry.data.repository

import com.cs330.smartpantry.data.remote.CustomRecipeApi
import com.cs330.smartpantry.model.CustomRecipe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class CustomRecipeRepository @Inject constructor(
    private val customRecipeApi: CustomRecipeApi
) {
    fun getAllCustomRecipes(): Flow<List<CustomRecipe>> = flow {

        val recipes = customRecipeApi.getAllCustomRecipes()
        emit(recipes)
    }.catch { e ->
        emit(emptyList())
    }
    suspend fun addRecipe(recipe: CustomRecipe) = customRecipeApi.addCustomRecipe(recipe)

    suspend fun updateRecipe(id: String, recipe: CustomRecipe) = customRecipeApi.updateCustomRecipe(id, recipe)

    suspend fun deleteRecipe(id: String) = customRecipeApi.deleteCustomRecipe(id)

    suspend fun getCustomRecipeById(id: String): CustomRecipe {
        return customRecipeApi.getAllCustomRecipes().first { it.id == id }
    }
}