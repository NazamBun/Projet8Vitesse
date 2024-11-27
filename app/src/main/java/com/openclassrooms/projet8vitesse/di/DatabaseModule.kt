package com.openclassrooms.projet8vitesse.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import android.content.Context
import com.openclassrooms.projet8vitesse.data.local.AppDatabase
import dagger.hilt.android.qualifiers.ApplicationContext

// Ce module configure Room pour l'injection de dépendances
@Module
@InstallIn(SingletonComponent::class)

object DatabaseModule {

    // Fournit l'instance de la base de données Room
    private const val DATABASE_NAME = "candidate_database"

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DATABASE_NAME
        ).build()
    }
    // Fournit le DAO pour accéder aux données
    @Provides
    @Singleton
    fun provideCandidateDao(database: AppDatabase) = database.candidateDao()
}