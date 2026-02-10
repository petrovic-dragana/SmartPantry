package com.cs330.smartpantry.data.remote


import com.cs330.smartpantry.model.MealResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MealApi {
    @GET("filter.php")
    suspend fun getMealsByIngredient(@Query("i") ingredient: String): MealResponse
    @GET("search.php")
    suspend fun getMealByName(@Query("s") name: String): MealResponse

    companion object {
        const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"
    }
    @GET("lookup.php")
    suspend fun getFullRecipeDetails(@Query("i") id: String): MealResponse

    @GET("filter.php")
    suspend fun getRecipesByCategory(@Query("c") category: String): MealResponse
}