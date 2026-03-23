package com.example.app.di

import android.content.Context
import androidx.room.Room
import com.example.app.data.local.AppDatabase
import com.example.app.data.local.FavoriteLocationDao
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
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "weather_app_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideFavoriteLocationDao(
        database: AppDatabase
    ): FavoriteLocationDao {
        return database.favoriteLocationDao()
    }
}