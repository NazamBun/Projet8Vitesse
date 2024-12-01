package com.openclassrooms.projet8vitesse.domain.usecase

import com.openclassrooms.projet8vitesse.data.repository.CandidateRepository
import com.openclassrooms.projet8vitesse.domain.model.Candidate
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use Case pour récupérer les candidats avec des filtres optionnels.
 *
 * @param repository Le repository utilisé pour accéder aux données des candidats.
 */
class GetCandidatesUseCase @Inject constructor(
    private val repository: CandidateRepository
) {

    /**
     * Exécuter le Use Case pour obtenir les candidats filtrés.
     *
     * @param favorite Filtrer par favori (true/false) ou null pour ignorer ce filtre.
     * @param name Filtrer par nom (partiel ou complet) ou null pour ignorer ce filtre.
     * @return Un flux contenant la liste des candidats correspondant aux critères.
     */
    operator fun invoke(favorite: Boolean?, name: String?) : Flow<List<Candidate>> {
        return repository.getCandidates(favorite, name)
    }
}