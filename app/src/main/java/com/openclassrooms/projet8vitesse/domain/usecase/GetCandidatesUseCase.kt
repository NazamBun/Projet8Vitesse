package com.openclassrooms.projet8vitesse.domain.usecase

import com.openclassrooms.projet8vitesse.domain.model.Candidate
import com.openclassrooms.projet8vitesse.data.repository.CandidateRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use Case pour récupérer les candidats avec des filtres optionnels.
 */
class GetCandidatesUseCase @Inject constructor(
    private val candidateRepository: CandidateRepository
) {

    /**
     * Récupère une liste de candidats selon les filtres spécifiés.
     * @param favorite Filtrer par favori (true/false) ou null pour ignorer.
     * @param name Filtrer par nom (partiel ou complet) ou null pour ignorer.
     * @return Un flux contenant la liste des candidats correspondant aux filtres.
     */
    fun execute(favorite: Boolean? = null, name: String? = null): Flow<List<Candidate>> {
        return candidateRepository.getCandidates(favorite, name)
    }
}
