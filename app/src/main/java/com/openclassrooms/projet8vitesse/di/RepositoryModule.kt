package com.openclassrooms.projet8vitesse.di

import com.openclassrooms.projet8vitesse.data.repository.CandidateRepository
import com.openclassrooms.projet8vitesse.data.repository.CandidateRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Module Hilt pour fournir les dépendances liées aux repositories.
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    /**
     * Fournit une instance de `CandidateRepository`.
     * @param repositoryImpl L'implémentation de CandidateRepository.
     */
    @Provides
    @Singleton
    fun provideCandidateRepository(
        repositoryImpl: CandidateRepositoryImpl
    ): CandidateRepository {
        return repositoryImpl
    }
}