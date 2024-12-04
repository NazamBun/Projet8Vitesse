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
 * ViewModel pour gérer les données et la logique de DetailScreen.
 */
@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: CandidateRepository
) : ViewModel() {

    // Flux contenant les données du candidat
    private val _candidate = MutableStateFlow<Candidate?>(null)
    val candidate: StateFlow<Candidate?> get() = _candidate

    /**
     * Charge les données du candidat depuis le repository.
     * @param candidateId L'ID du candidat à charger.
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
                loadCandidate(candidate.id!!)
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
            }
        }
    }
}
