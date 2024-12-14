package com.openclassrooms.projet8vitesse.ui.detailscreen

import com.openclassrooms.projet8vitesse.domain.model.Candidate

/**
 * Représente les différents états de l'interface utilisateur pour l'écran de détail.
 */
sealed class DetailUiState {

    /**
     * État de chargement des données.
     */
    object Loading : DetailUiState()

    /**
     * État affichant les informations du candidat.
     * @param candidate Les détails du candidat.
     */
    data class Success(
        val candidate: Candidate,
        val convertedSalary: String
    ) : DetailUiState()

    /**
     * État affichant une erreur.
     * @param message Le message d'erreur à afficher.
     */
    data class Error(val message: String) : DetailUiState()
}
