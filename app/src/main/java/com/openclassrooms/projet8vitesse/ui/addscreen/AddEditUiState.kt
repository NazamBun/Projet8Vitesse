package com.openclassrooms.projet8vitesse.ui.addscreen

/**
 * Représente les différents états de l'interface utilisateur pour le fragment Add/Edit.
 */
sealed class AddEditUiState {
    /**
     * État initial ou inactif.
     */
    object Idle : AddEditUiState()

    /**
     * État de chargement, utilisé lorsque des opérations sont en cours.
     */
    object Loading : AddEditUiState()

    /**
     * État de succès, contient un message informant l'utilisateur.
     *
     * @param message Le message de succès.
     * @param candidateId L'ID du candidat inséré.
     */
    data class Success(val message: String, val candidateId: Long) : AddEditUiState()

    /**
     * État d'erreur, contient un message d'erreur.
     *
     * @param error Le message d'erreur.
     */
    data class Error(val error: String) : AddEditUiState()
}
