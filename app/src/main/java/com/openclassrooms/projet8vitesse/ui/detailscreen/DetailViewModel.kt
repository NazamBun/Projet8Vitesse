package com.openclassrooms.projet8vitesse.ui.detailscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.projet8vitesse.data.local.entities.CandidateEntity
import com.openclassrooms.projet8vitesse.repository.CandidateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

// ViewModel pour gérer les données et la logique de DetailScreen
@HiltViewModel
class DetailViewModel @Inject constructor(val repository: CandidateRepository) : ViewModel() {

    // Supprimer un candidat
    fun deleteCandidate(candidate: CandidateEntity) {
        viewModelScope.launch {
            repository.deleteCandidate(candidate)
        }
    }

    // Ajouter ou retirer un candidat des favoris
    fun toggleFavorite(candidate: CandidateEntity) {
        viewModelScope.launch {
            val updatedCandidate = candidate.copy(isFavorite = !candidate.isFavorite)
            repository.insertCandidate(updatedCandidate)
        }
    }
}