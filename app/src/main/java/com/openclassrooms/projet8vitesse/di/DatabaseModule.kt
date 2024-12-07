package com.openclassrooms.projet8vitesse.di

import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import android.content.Context
import com.openclassrooms.projet8vitesse.data.dao.CandidateDao
import com.openclassrooms.projet8vitesse.data.database.AppDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

// Ce module configure Room pour l'injection de dépendances
@Module
@InstallIn(SingletonComponent::class)

object DatabaseModule {

    @Provides
    @Singleton
    fun provideCoroutineScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        coroutineScope: CoroutineScope
    ): AppDatabase {
        return AppDatabase.getDatabase(context, coroutineScope)
    }

    @Provides
    fun provideCandidateDAO(appDatabase: AppDatabase): CandidateDao {
        return appDatabase.candidateDao()
    }

}