package com.openclassrooms.projet8vitesse.di

import com.openclassrooms.projet8vitesse.repository.CandidateRepository
import com.openclassrooms.projet8vitesse.repository.CandidateRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    // Associe CandidateRepositoryImpl à CandidateRepository
    @Binds
    @Singleton
    abstract fun binCandidateRepository(
        candidateRepositoryImpl: CandidateRepositoryImpl
    ): CandidateRepository
}