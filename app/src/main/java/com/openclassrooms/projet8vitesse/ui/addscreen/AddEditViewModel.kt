package com.openclassrooms.projet8vitesse.ui.addscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.projet8vitesse.data.repository.CandidateRepository
import com.openclassrooms.projet8vitesse.domain.model.Candidate
import com.openclassrooms.projet8vitesse.domain.usecase.InsertCandidateUseCase
import com.openclassrooms.projet8vitesse.domain.usecase.UpdateCandidateUseCase
import com.openclassrooms.projet8vitesse.domain.usecase.UpdateFavoriteStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel pour gérer les données et la logique du fragment Add/Edit.
 *
 * Cette classe est responsable de :
 * - Insérer un nouveau candidat.
 * - Mettre à jour les informations d'un candidat existant.
 * - Gérer l'état des opérations (succès, échec, etc.).
 */@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val insertCandidateUseCase: InsertCandidateUseCase,
    private val updateCandidateUseCase: UpdateCandidateUseCase
) : ViewModel() {

    // État actuel de l'opération (succès, erreur, chargement, etc.).
    private val _uiState = MutableStateFlow<AddEditUiState>(AddEditUiState.Idle)
    val uiState: StateFlow<AddEditUiState> = _uiState

    /**
     * Insère un nouveau candidat dans la base de données.
     *
     * @param candidate Le candidat à insérer.
     */
    fun insertCandidate(candidate: Candidate) {
        viewModelScope.launch {
            _uiState.value = AddEditUiState.Loading
            try {
                val result = insertCandidateUseCase.invoke(candidate)
                _uiState.value = AddEditUiState.Success("Candidate inserted with ID: $result")
            } catch (e: Exception) {
                _uiState.value = AddEditUiState.Error("Error inserting candidate: ${e.message}")
            }
        }
    }

    /**
     * Met à jour les informations d'un candidat existant.
     *
     * @param candidate Le candidat à mettre à jour.
     */
    fun updateCandidate(candidate: Candidate) {
        viewModelScope.launch {
            _uiState.value = AddEditUiState.Loading
            try {
                val rowsUpdated = updateCandidateUseCase.invoke(candidate)
                if (rowsUpdated > 0) {
                    _uiState.value = AddEditUiState.Success("Candidate updated successfully")
                } else {
                    _uiState.value = AddEditUiState.Error("No rows were updated.")
                }
            } catch (e: Exception) {
                _uiState.value = AddEditUiState.Error("Error updating candidate: ${e.message}")
            }
        }
    }
}