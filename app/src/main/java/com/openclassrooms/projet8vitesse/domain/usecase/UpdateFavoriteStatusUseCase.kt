package com.openclassrooms.projet8vitesse.domain.usecase

import com.openclassrooms.projet8vitesse.data.repository.CandidateRepository
import javax.inject.Inject

/**
 * Use Case pour mettre à jour le statut de favori d'un candidat.
 *
 * @property candidateRepository Le repository permettant d'accéder aux données des candidats.
 */
class UpdateFavoriteStatusUseCase @Inject constructor(
    private val candidateRepository: CandidateRepository
) {
    /**
     * Exécute la mise à jour du statut de favori d'un candidat.
     *
     * @param id L'identifiant du candidat à mettre à jour.
     * @param favorite Le nouveau statut de favori.
     * @return Le nombre de lignes affectées.
     */
    suspend fun execute(id: Long, favorite: Boolean): Int {
        return candidateRepository.updateFavoriteStatus(id, favorite)
    }
}