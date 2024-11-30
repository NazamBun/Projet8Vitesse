package com.openclassrooms.projet8vitesse.data.repository

import com.openclassrooms.projet8vitesse.data.entity.CandidateDto
import kotlinx.coroutines.flow.Flow

// Interface définissant les opérations sur les candidats
interface CandidateRepository {

    // Récupérer tous les candidats
    fun getAllCandidates(): Flow<List<CandidateDto>>

    // Récupérer les candidats favoris
    fun getFavoriteCandidates(): Flow<List<CandidateDto>>

    // Ajouter ou mettre à jour un candidat
    suspend fun insertCandidate(candidate: CandidateDto)

    // Supprimer un candidat
    suspend fun deleteCandidate(candidate: CandidateDto)

    // Supprimer tous les candidats
    suspend fun deleteAllCandidates()
}