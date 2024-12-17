package com.openclassrooms.projet8vitesse.di

import com.openclassrooms.projet8vitesse.data.repository.CandidateRepository
import com.openclassrooms.projet8vitesse.data.repository.CandidateRepositoryImpl
import com.openclassrooms.projet8vitesse.data.repository.CurrencyRepository
import com.openclassrooms.projet8vitesse.data.repository.CurrencyRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Module Hilt pour fournir les dépendances liées aux repositories.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * Fournit une instance de `CandidateRepository`.
     * @param repositoryImpl L'implémentation de CandidateRepository.
     */
    @Binds
    @Singleton
    abstract fun bindCandidateRepository(
        repositoryImpl: CandidateRepositoryImpl
    ): CandidateRepository

    /**
     * Fournit une instance de `CurrencyRepository`.
     * @param repositoryImpl L'implémentation de CurrencyRepository.
     */
    @Binds
    @Singleton
    abstract fun bindCurrencyRepository(
        repositoryImpl: CurrencyRepositoryImpl
    ): CurrencyRepository
}
