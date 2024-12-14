package com.openclassrooms.projet8vitesse.data.repository

import com.openclassrooms.projet8vitesse.domain.model.Candidate
import kotlinx.coroutines.flow.Flow

/**
 * Interface définissant les opérations sur les candidats.
 */
interface CandidateRepository {

    /**
     * Récupérer tous les candidats.
     * @return Un flux contenant la liste des candidats.
     */
    fun getAllCandidates(): Flow<List<Candidate>>

    /**
     * Récupérer les candidats avec des filtres optionnels.
     * @param favorite Filtrer par favori (true/false) ou null pour ignorer ce filtre.
     * @param name Filtrer par nom (partiel ou complet) ou null pour ignorer ce filtre.
     * @return Un flux contenant la liste des candidats correspondant aux critères.
     */
    fun getCandidates(favorite: Boolean?, name: String?): Flow<List<Candidate>>

    /**
     * Récupérer un candidat par son ID.
     * @param id L'identifiant unique du candidat.
     * @return Un flux contenant le candidat correspondant.
     */
    fun getById(id: Long): Flow<Candidate?>

    /**
     * Ajouter ou mettre à jour un candidat.
     * @param candidate Le modèle de domaine du candidat à insérer.
     * @return L'identifiant du candidat inséré.
     */
    suspend fun insertCandidate(candidate: Candidate): Long

    /**
     * Mettre à jour les informations d'un candidat.
     * @param candidate Le modèle de domaine du candidat à mettre à jour.
     * @return Le nombre de lignes affectées.
     */
    suspend fun updateCandidate(candidate: Candidate): Int

    /**
     * Supprimer un candidat.
     * @param candidate Le modèle de domaine du candidat à supprimer.
     */
    suspend fun deleteCandidate(candidate: Candidate)

    /**
     * Supprimer tous les candidats.
     */
    suspend fun deleteAllCandidates()

    /**
     * Mettre à jour le statut de favori d'un candidat.
     * @param id L'identifiant du candidat.
     * @param favorite Le nouveau statut de favori.
     * @return Le nombre de lignes affectées.
     */
    suspend fun updateFavoriteStatus(id: Long, favorite: Boolean): Int
}