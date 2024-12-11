package com.openclassrooms.projet8vitesse.ui.addscreen

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.projet8vitesse.data.repository.CandidateRepository
import com.openclassrooms.projet8vitesse.domain.model.Candidate
import com.openclassrooms.projet8vitesse.domain.usecase.InsertCandidateUseCase
import com.openclassrooms.projet8vitesse.domain.usecase.UpdateCandidateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.threeten.bp.Instant
import java.util.Date
import javax.inject.Inject

/**
 * ViewModel pour gérer les données et la logique du fragment Add/Edit.
 *
 * Cette classe est responsable de :
 * - Mettre à jour les informations du candidat en cours.
 * - Valider les données du candidat.
 * - Insérer un nouveau candidat dans la base de données.
 * - Gérer l'état de l'interface utilisateur (chargement, succès, erreur).
 */
@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val insertCandidateUseCase: InsertCandidateUseCase,
    private val updateCandidateUseCase: UpdateCandidateUseCase,
    private val repository: CandidateRepository
) : ViewModel() {

    // État actuel de l'opération (succès, erreur, chargement, etc.).
    private val _uiState = MutableStateFlow<AddEditUiState>(AddEditUiState.Idle)
    val uiState: StateFlow<AddEditUiState> = _uiState

    private var candidatePhoto: Bitmap? = null
    private var candidateDateOfBirth: Instant? = null

    private val candidateData = MutableStateFlow(Candidate())
    val candidateDataFlow = candidateData.asStateFlow()

    /**
     * Met à jour la photo du candidat.
     * @param photo Le bitmap représentant la photo sélectionnée.
     */
    fun updatePhoto(photo: Bitmap) {
        candidatePhoto = photo
    }

    /**
     * Met à jour la date de naissance du candidat.
     * @param instant L'instant représentant la date de naissance sélectionnée.
     */
    fun updateDateOfBirth(instant: Instant) {
        candidateDateOfBirth = instant
    }

    /**
     * Charge un candidat existant pour l'édition.
     * @param candidateId L'ID du candidat à charger.
     */
    fun loadCandidateForEdit(candidateId: Long) {
        viewModelScope.launch {
            repository.getById(candidateId).collect { candidate ->
                // Met à jour candidateData, ainsi que la photo et la date
                candidateData.value = candidate
                candidatePhoto = candidate.photo
                candidateDateOfBirth = candidate.dateOfBirth
            }
        }
    }

    /**
     * Met à jour les données textuelles du candidat (prénom, nom, téléphone, email, salaire, notes).
     * Cette méthode est appelée après la validation des champs dans le fragment.
     *
     * @param firstName Prénom du candidat.
     * @param lastName Nom du candidat.
     * @param phoneNumber Numéro de téléphone du candidat.
     * @param email Adresse email du candidat.
     * @param expectedSalary Salaire attendu du candidat (en nombre entier).
     * @param notes Notes relatives au candidat.
     */
    fun updateCandidateData(
        firstName: String,
        lastName: String,
        phoneNumber: String,
        email: String,
        expectedSalary: Int,
        notes: String
    ) {
        val current = candidateData.value
        candidateData.value = current.copy(
            firstName = firstName,
            lastName = lastName,
            phoneNumber = phoneNumber,
            email = email,
            expectedSalary = expectedSalary,
            note = notes
        )
    }

    fun onSaveCandidate() {
        val candidate = candidateData.value.copy(
            photo = candidatePhoto,
            dateOfBirth = candidateDateOfBirth?:Instant.now()
        )

        if (validateCandidate(candidate)) {
            insertCandidate(candidate)
        } else {
            _uiState.value = AddEditUiState.Error("Invalid candidate data")
        }
    }

    /**
     * Vérifie que les champs obligatoires du candidat sont remplis et valides.
     * @param candidate Le candidat à valider.
     * @return true si le candidat est valide, false sinon.
     */
    private fun validateCandidate(candidate: Candidate): Boolean {
        return candidate.firstName.isNotBlank() &&
                candidate.lastName.isNotBlank() &&
                candidate.phoneNumber.isNotBlank() &&
                candidate.email.isNotBlank() &&
                candidate.dateOfBirth != Instant.EPOCH
    }

    /**
     * Insère un candidat dans la base de données.
     * @param candidate Les informations du candidat à insérer.
     */
    private fun insertCandidate(candidate: Candidate) {
        viewModelScope.launch {
            _uiState.value = AddEditUiState.Loading
            try {
                val id = insertCandidateUseCase.invoke(candidate)
                if (id > 0) {
                    _uiState.value = AddEditUiState.Success("Candidate added successfully!", id)
                } else {
                    _uiState.value = AddEditUiState.Error("Failed to add candidate.")
                }
            } catch (e: Exception) {
                _uiState.value = AddEditUiState.Error(e.message ?: "Unexpected error.")
            }
        }
    }
}