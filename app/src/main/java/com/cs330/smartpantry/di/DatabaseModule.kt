package com.cs330.smartpantry.di

import android.content.Context
import androidx.room.Room
import com.cs330.smartpantry.data.local.AppDatabase
import com.cs330.smartpantry.data.local.PantryDAO
import com.cs330.smartpantry.data.remote.CustomRecipeApi
import com.cs330.smartpantry.data.remote.MealApi
import com.cs330.smartpantry.data.repository.PantryRepository
import com.cs330.smartpantry.model.CustomRecipe
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase{
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "smart_pantry_db"
        ).build()
    }
    @Provides
    fun providePantryDao(database: AppDatabase): PantryDAO{
        return database.pantryDao()
    }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit{
        return Retrofit.Builder()
            .baseUrl(MealApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideMealApi(retrofit: Retrofit): MealApi{
        return retrofit.create(MealApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRepository(pantryDAO: PantryDAO, mealApi: MealApi): PantryRepository{
        return PantryRepository(pantryDAO, mealApi)
    }

    @Provides
    @Singleton
    @Named("CustomRetrofit")
    fun provideCustomRetrofit(): Retrofit{
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideCustomRecipeApi(@Named("CustomRetrofit") retrofit: Retrofit): CustomRecipeApi{
        return retrofit.create(CustomRecipeApi::class.java)
    }
}