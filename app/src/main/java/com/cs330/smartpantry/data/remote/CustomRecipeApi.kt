package com.cs330.smartpantry.data.remote

import com.cs330.smartpantry.model.CustomRecipe
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CustomRecipeApi {

    @GET("custom-recipes")
    suspend fun getAllCustomRecipes(): List<CustomRecipe>

    @POST("custom-recipes")
    suspend fun addCustomRecipe(@Body recipe: CustomRecipe): CustomRecipe

    @PUT("custom-recipes/{id}")
    suspend fun updateCustomRecipe(
        @Path("id") id: String,
        @Body recipe: CustomRecipe
    ): CustomRecipe

    @DELETE("custom-recipes/{id}")
    suspend fun deleteCustomRecipe(@Path("id") id: String)
}