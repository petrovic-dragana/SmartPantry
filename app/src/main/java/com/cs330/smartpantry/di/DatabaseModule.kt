package com.cs330.smartpantry.di

import android.content.Context
import androidx.room.Room
import com.cs330.smartpantry.data.local.AppDatabase
import com.cs330.smartpantry.data.local.PantryDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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
}