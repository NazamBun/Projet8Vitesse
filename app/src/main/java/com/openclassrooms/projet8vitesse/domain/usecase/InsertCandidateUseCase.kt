package com.openclassrooms.projet8vitesse.domain.usecase

import com.openclassrooms.projet8vitesse.data.repository.CandidateRepository
import com.openclassrooms.projet8vitesse.domain.model.Candidate
import javax.inject.Inject

/**
 * Use Case pour insérer un candidat dans la base de données.
 * @property repository Le repository utilisé pour insérer les données.
 */
class InsertCandidateUseCase @Inject constructor(
    private val repository: CandidateRepository
) {
    /**
     * Invoquer l'insertion d'un candidat dans la base de données.
     * @param candidate Le candidat à insérer.
     * @return L'identifiant du candidat inséré.
     */
    suspend fun invoke(candidate: Candidate): Long {
        return repository.insertCandidate(candidate)

    }
}