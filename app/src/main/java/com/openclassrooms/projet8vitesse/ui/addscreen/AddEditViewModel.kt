package com.openclassrooms.projet8vitesse.ui.addscreen

import android.graphics.Bitmap
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.projet8vitesse.data.repository.CandidateRepository
import com.openclassrooms.projet8vitesse.domain.model.Candidate
import com.openclassrooms.projet8vitesse.domain.usecase.GetCandidateByIdUseCase
import com.openclassrooms.projet8vitesse.domain.usecase.InsertCandidateUseCase
import com.openclassrooms.projet8vitesse.domain.usecase.UpdateCandidateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.threeten.bp.Instant
import java.util.Date
import java.util.regex.Pattern
import javax.inject.Inject


/**
 * ViewModel pour l'écran Add/Edit Candidate.
 *
 * Objectif : Gérer la logique pour l'ajout ou la modification d'un candidat.
 * On applique les bonnes pratiques :
 * - MVVM : Ce ViewModel ne touche pas à l'UI directement, il expose un état (UiState).
 * - Clean Architecture : Le ViewModel dépend de Use Cases, pas de la couche de données directement.
 * - SOLID : Une seule responsabilité, pas de logique de navigation, pas de logique UI complexe.
 * - Code facile : Variables claires, commentaires simples, pas de complexité inutile.
 *
 * User stories (pour AddEditScreen) :
 * - Afficher une top app bar avec "Ajouter un candidat" ou "Modifier un candidat".
 * - Icône pour revenir en arrière.
 * - Permettre d'ajouter ou changer la photo du candidat (placeholder par défaut).
 * - Champs obligatoires : prénom, nom, téléphone, email, date de naissance.
 * - Champs facultatifs : salaire, notes.
 * - Vérifier que les champs obligatoires ne soient pas vides.
 * - Vérifier le format de l'email.
 * - Bouton "Sauvegarder" pour insérer ou mettre à jour en base via les Use Cases.
 * - Si succès : fermer l'écran et revenir à l'accueil.
 *
 * Les Use Cases injectés :
 * - InsertCandidateUseCase : pour ajouter un candidat
 * - GetCandidateByIdUseCase : pour charger un candidat existant (mode édition)
 * - UpdateCandidateUseCase : pour mettre à jour un candidat existant
 */
@HiltViewModel
class AddEditViewModel @Inject constructor(
    /**
     * Use case pour insérer un candidat dans la base de données.
     * Utilisé lorsqu'on ajoute un nouveau candidat.
     */
    private val insertCandidateUseCase: InsertCandidateUseCase,

    /**
     * Use case pour récupérer un candidat grâce à son identifiant.
     * Utilisé lorsqu'on est en mode édition pour pré-remplir les champs.
     */
    private val getCandidateByIdUseCase: GetCandidateByIdUseCase,

    /**
     * Use case pour mettre à jour un candidat existant.
     * Utilisé en mode édition, quand l'utilisateur modifie les informations d'un candidat.
     */
    private val updateCandidateUseCase: UpdateCandidateUseCase
) : ViewModel() {
    // État de l'UI : on part d'un état Idle sans erreur, sans chargement, sans données pré-remplies
    private val _uiState = MutableStateFlow<AddEditUiState>(AddEditUiState.Idle)
    val uiState: StateFlow<AddEditUiState> = _uiState

    // Variables pour stocker les données du formulaire
    private var candidateId: Long = -1L
    private var firstName: String = ""
    private var lastName: String = ""
    private var phone: String = ""
    private var email: String = ""
    private var dateOfBirth: Instant? = null
    private var salary: Int? = null
    private var notes: String = ""
    private var photo: Bitmap? = null

    // Méthode pour initialiser l'écran
    // Si on passe un candidateId existant, on charge le candidat et pré-remplit les champs.
    // Sinon, on est en mode ajout.
    fun init(candidateId: Long) {
        if (candidateId <= 0) {
            // Mode Ajout
            this.candidateId = -1
            // On affiche "Ajouter un candidat"
            _uiState.value = AddEditUiState.Loaded(
                titleResId = com.openclassrooms.projet8vitesse.R.string.add_candidate,
                isEditing = false,
                photo = null, // Placeholder par défaut géré par le Fragment
                firstName = "",
                lastName = "",
                phone = "",
                email = "",
                dateOfBirth = null,
                salary = "",
                notes = ""
            )
        } else {
            // Mode Édition
            this.candidateId = candidateId
            loadCandidate(candidateId)
        }
    }

    // Charge un candidat existant pour le mode édition
    private fun loadCandidate(id: Long) {
        _uiState.value = AddEditUiState.Loading
        viewModelScope.launch {
            getCandidateByIdUseCase.execute(id).collect { candidate ->
                if (candidate == null) {
                    // Aucune donnée => Erreur
                    _uiState.value = AddEditUiState.Error("Candidat introuvable")
                } else {
                    // On remplit nos variables internes
                    firstName = candidate.firstName
                    lastName = candidate.lastName
                    phone = candidate.phoneNumber
                    email = candidate.email
                    dateOfBirth = candidate.dateOfBirth
                    salary = candidate.expectedSalary
                    notes = candidate.note ?: ""
                    photo = candidate.photo
                    // On met à jour l'UI avec les données existantes
                    _uiState.value = AddEditUiState.Loaded(
                        titleResId = com.openclassrooms.projet8vitesse.R.string.edit_candidate,
                        isEditing = true,
                        photo = photo,
                        firstName = firstName,
                        lastName = lastName,
                        phone = phone,
                        email = email,
                        dateOfBirth = dateOfBirth,
                        salary = salary?.toString() ?: "",
                        notes = notes
                    )
                }
            }
        }
    }

    // Méthodes pour mettre à jour les champs :

    fun onFirstNameChanged(value: String) {
        firstName = value
    }

    fun onLastNameChanged(value: String) {
        lastName = value
    }

    fun onPhoneChanged(value: String) {
        phone = value
    }

    fun onEmailChanged(value: String) {
        email = value
    }

    fun onDateOfBirthSelected(value: Instant) {
        dateOfBirth = value
    }

    fun onSalaryChanged(value: String) {
        salary = value.toIntOrNull() // Si non convertible, null
    }

    fun onNotesChanged(value: String) {
        notes = value
    }

    fun onPhotoSelected(bitmap: Bitmap) {
        photo = bitmap
    }

    // Méthode déclenchée lorsque l'utilisateur clique sur "Sauvegarder"
    // On vérifie les champs obligatoires et le format de l'email.
    // Si OK, on insère ou on met à jour.
    // Puis on signale le succès pour fermer l'écran.
    fun onSaveClicked() {
        val emptyFields = mutableListOf<AddEditUiState.MandatoryField>()

        if (firstName.isBlank()) emptyFields.add(AddEditUiState.MandatoryField.FIRST_NAME)
        if (lastName.isBlank()) emptyFields.add(AddEditUiState.MandatoryField.LAST_NAME)
        if (phone.isBlank()) emptyFields.add(AddEditUiState.MandatoryField.PHONE)
        if (email.isBlank()) emptyFields.add(AddEditUiState.MandatoryField.EMAIL)
        if (dateOfBirth == null) emptyFields.add(AddEditUiState.MandatoryField.DATE_OF_BIRTH)

        if (emptyFields.isNotEmpty()) {
            _uiState.value = AddEditUiState.ErrorMandatoryFields(
                "Veuillez remplir tous les champs obligatoires.",
                emptyFields
            )
            return
        }

        // Vérifier le format de l'email
        if (!isEmailValid(email)) {
            _uiState.value = AddEditUiState.ErrorEmailFormat("Format d'email invalide.")
            return
        }

        // Tout est bon, on peut sauvegarder
        _uiState.value = AddEditUiState.Loading

        val candidateToSave = Candidate(
            id = if (candidateId > 0) candidateId else null,
            firstName = firstName,
            lastName = lastName,
            photo = photo, // S'il est null, on affichera placeholder côté UI
            phoneNumber = phone,
            email = email,
            dateOfBirth = dateOfBirth!!, // Non null car checké
            expectedSalary = salary ?: 0,
            note = notes,
            isFavorite = false
        )

        viewModelScope.launch {
            try {
                if (candidateId > 0) {
                    // Mise à jour
                    updateCandidateUseCase.invoke(candidateToSave)
                    _uiState.value = AddEditUiState.Success("Candidat mis à jour avec succès")
                } else {
                    // Ajout
                    insertCandidateUseCase.invoke(candidateToSave)
                    _uiState.value = AddEditUiState.Success("Candidat ajouté avec succès")
                }
            } catch (e: Exception) {
                _uiState.value = AddEditUiState.Error("Erreur lors de la sauvegarde.")
            }
        }
    }

    // Vérification de l'email via un pattern
    private fun isEmailValid(email: String): Boolean {
        val emailPattern = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@(.+)$"
        )
        return emailPattern.matcher(email).matches()
    }
}