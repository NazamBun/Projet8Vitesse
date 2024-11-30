package com.openclassrooms.projet8vitesse.ui.addscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.projet8vitesse.data.entity.CandidateDto
import com.openclassrooms.projet8vitesse.data.repository.CandidateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

// ViewModel pour gérer les données et la logique de Add/EditScreen
@HiltViewModel
class AddEditViewModel @Inject constructor(val repository: CandidateRepository) : ViewModel() {

    // Ajouter ou mettre à jour un candidat
    fun saveCandidate(candidate: CandidateDto) {
        viewModelScope.launch {
            repository.insertCandidate(candidate)
        }
    }
}