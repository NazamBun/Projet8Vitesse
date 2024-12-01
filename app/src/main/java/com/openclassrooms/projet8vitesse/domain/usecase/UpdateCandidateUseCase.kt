package com.openclassrooms.projet8vitesse.domain.usecase

import com.openclassrooms.projet8vitesse.data.repository.CandidateRepository
import com.openclassrooms.projet8vitesse.domain.model.Candidate
import javax.inject.Inject

class UpdateCandidateUseCase @Inject constructor(
    private val repository: CandidateRepository
) {
    /**
     * Invoquer la mise à jour d'un candidat dans la base de données.
     * @param candidate Le candidat à mettre à jour.
     * @return Le nombre de lignes affectées.
     */
    suspend fun invoke(candidate: Candidate): Int {
        return repository.updateCandidate(candidate)
    }
}