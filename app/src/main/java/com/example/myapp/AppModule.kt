package com.example.myapp

import android.content.Context
import androidx.room.Room
import com.example.myapp.Dao.ImageModelDAO
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton
import com.example.myapp.Database.AppDatabase
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "image_database"
        ).build()
    }

    @Provides
    fun provideImageModelDAO(database: AppDatabase): ImageModelDAO {
        return database.displayImageModelDao()
    }
}
