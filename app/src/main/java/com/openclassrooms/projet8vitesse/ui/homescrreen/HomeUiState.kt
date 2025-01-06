package com.openclassrooms.projet8vitesse.ui.homescrreen

import com.openclassrooms.projet8vitesse.domain.model.Candidate

/**
 * Représente les différents états de l'interface utilisateur pour l'écran d'accueil.
 */
sealed class HomeUiState {

    /**
     * État chargé lorsque les données sont en cours de chargement.
     */
    // état initial
    data object Loading : HomeUiState()

    /**
     * État chargé lorsque la liste des candidats est vide.
     */
    // état vide
    data object Empty : HomeUiState()

    /**
     * État chargé lorsque les candidats sont disponibles.
     * @param candidates Liste des candidats à afficher.
     */
    // état succès
    data class Success(val candidates: List<Candidate>) : HomeUiState()

    /**
     * État chargé lorsqu'une erreur survient.
     * @param message Le message d'erreur à afficher.
     */
    data class Error(val message: String) : HomeUiState()
}