package com.cs330.smartpantry.data.remote


import com.cs330.smartpantry.model.MealResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MealApi {
    @GET("filter.php")
    suspend fun getMealsByIngredient(@Query("i") ingredient: String): MealResponse

    companion object {
        const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"
    }
}