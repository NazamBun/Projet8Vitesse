package com.openclassrooms.projet8vitesse.repository

import com.openclassrooms.projet8vitesse.data.local.entities.CandidateEntity
import kotlinx.coroutines.flow.Flow

// Interface définissant les opérations sur les candidats
interface CandidateRepository {

    // Récupérer tous les candidats
    fun getAllCandidates(): Flow<List<CandidateEntity>>

    // Récupérer les candidats favoris
    fun getFavoriteCandidates(): Flow<List<CandidateEntity>>

    // Ajouter ou mettre à jour un candidat
    suspend fun insertCandidate(candidate: CandidateEntity)

    // Supprimer un candidat
    suspend fun deleteCandidate(candidate: CandidateEntity)

    // Supprimer tous les candidats
    suspend fun deleteAllCandidates()
}