package com.openclassrooms.projet8vitesse.ui.addscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.projet8vitesse.domain.model.Candidate
import com.openclassrooms.projet8vitesse.domain.usecase.InsertCandidateUseCase
import com.openclassrooms.projet8vitesse.domain.usecase.UpdateCandidateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

/**
 * ViewModel pour gérer les données et la logique du fragment Add/Edit.
 *
 * Cette classe est responsable de :
 * - Insérer un nouveau candidat.
 * - Mettre à jour les informations d'un candidat existant.
 * - Gérer l'état des opérations (succès, échec, etc.).
 */
@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val insertCandidateUseCase: InsertCandidateUseCase,
    private val updateCandidateUseCase: UpdateCandidateUseCase
) : ViewModel() {

    // État actuel de l'opération (succès, erreur, chargement, etc.).
    private val _uiState = MutableStateFlow<AddEditUiState>(AddEditUiState.Idle)
    val uiState: StateFlow<AddEditUiState> = _uiState

    /**
     * Insère un candidat dans la base de données.
     * @param candidate Les informations du candidat à insérer.
     */
    fun insertCandidate(candidate: Candidate) {
        // Vérifier les champs obligatoires
        if (candidate.firstName.isBlank() || candidate.lastName.isBlank() ||
            candidate.phoneNumber.isBlank() || candidate.email.isBlank() ||
            candidate.dateOfBirth == Instant.EPOCH
        ) {
            _uiState.value = AddEditUiState.Error("All fields are mandatory.")
            return
        }

        viewModelScope.launch {
            _uiState.value = AddEditUiState.Loading
            try {
                val id = insertCandidateUseCase.invoke(candidate)
                if (id > 0) {
                    _uiState.value = AddEditUiState.Success("Candidate added successfully!")
                } else {
                    _uiState.value = AddEditUiState.Error("Failed to add candidate.")
                }
            } catch (e: Exception) {
                _uiState.value = AddEditUiState.Error(e.message ?: "An unexpected error occurred.")
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