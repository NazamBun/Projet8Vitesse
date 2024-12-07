package com.openclassrooms.projet8vitesse.ui.detailscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.projet8vitesse.domain.model.Candidate
import com.openclassrooms.projet8vitesse.data.repository.CandidateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel pour gérer les données et la logique de l'écran de détail (DetailScreen).
 *
 * Il permet de :
 * - Charger les informations d'un candidat à partir de son ID.
 * - Actualiser le statut "favori" du candidat.
 * - Supprimer le candidat.
 * - Gérer la navigation vers l'écran d'édition et le retour après suppression.
 */
@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: CandidateRepository
) : ViewModel() {

    // Flux contenant les données du candidat
    private val _candidate = MutableStateFlow<Candidate?>(null)
    val candidate: StateFlow<Candidate?> get() = _candidate

    // État de navigation pour l'édition
    private val _navigateToEdit = MutableStateFlow(false)
    val navigateToEdit: StateFlow<Boolean> get() = _navigateToEdit

    // État de navigation après suppression
    private val _navigateBackAfterDelete = MutableStateFlow(false)
    val navigateBackAfterDelete: StateFlow<Boolean> get() = _navigateBackAfterDelete

    /**
     * Charge les données du candidat depuis le repository en fonction de l'ID fourni.
     *
     * @param candidateId L'ID du candidat à afficher.
     * Ce flux émettra les données du candidat dès qu'elles seront disponibles.
     */
    fun loadCandidate(candidateId: Long) {
        viewModelScope.launch {
            repository.getById(candidateId).collect { candidate ->
                _candidate.value = candidate
            }
        }
    }

    /**
     * Bascule le statut de favori pour le candidat.
     */
    fun toggleFavoriteStatus() {
        _candidate.value?.let { candidate ->
            viewModelScope.launch {
                repository.updateFavoriteStatus(candidate.id!!, !candidate.isFavorite)
                loadCandidate(candidate.id!!) // Recharge les données pour avoir l'état mis à jour
            }
        }
    }

    /**
     * Supprime le candidat de la base de données.
     */
    fun deleteCandidate() {
        _candidate.value?.let { candidate ->
            viewModelScope.launch {
                repository.deleteCandidate(candidate)
                // Déclenche une navigation en arrière
                _navigateBackAfterDelete.value = true
            }
        }
    }

    /**
     * Déclenche la navigation vers l'écran d'édition.
     * (Déplacé depuis le Fragment)
     */
    fun navigateToEdit() {
        _navigateToEdit.value = true
    }

    /**
     * Réinitialise les états de navigation.
     */
    fun resetNavigationFlags() {
        _navigateToEdit.value = false
        _navigateBackAfterDelete.value = false
    }
}
