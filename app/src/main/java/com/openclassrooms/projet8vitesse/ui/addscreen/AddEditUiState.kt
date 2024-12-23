package com.openclassrooms.projet8vitesse.ui.addscreen

import android.graphics.Bitmap
import org.threeten.bp.Instant

/**
 * État de l'interface utilisateur pour l'écran Add/Edit.
 *
 * On utilise une sealed class pour représenter les différents états.
 * Le Fragment va observer cet état et réagir en fonction.
 */
sealed class AddEditUiState {

    /**
     * État initial, rien n'est encore fait, ni chargé.
     */
    object Idle : AddEditUiState()

    /**
     * État de chargement, par exemple lors du chargement d'un candidat existant
     * ou pendant la sauvegarde.
     */
    object Loading : AddEditUiState()

    /**
     * État chargé avec toutes les données nécessaires pour afficher le formulaire.
     *
     * @param titleResId L'ID de la chaîne de caractères pour le titre (par exemple R.string.add_candidate ou R.string.edit_candidate).
     * @param isEditing Indique si on est en mode édition (true) ou ajout (false).
     * @param photo La photo du candidat s'il y en a une, sinon null pour laisser le placeholder.
     * @param firstName Prénom à afficher ou valeur par défaut en mode ajout.
     * @param lastName Nom à afficher ou valeur par défaut en mode ajout.
     * @param phone Téléphone à afficher ou vide.
     * @param email Email à afficher ou vide.
     * @param dateOfBirth Date de naissance ou null si non sélectionnée.
     * @param salary Salaire au format String (peut être vide si pas saisi).
     * @param notes Notes au format String (peut être vide si pas saisi).
     */
    data class Loaded(
        val titleResId: Int,
        val isEditing: Boolean,
        val photo: Bitmap?,
        val firstName: String,
        val lastName: String,
        val phone: String,
        val email: String,
        val dateOfBirth: Instant?,
        val salary: String,
        val notes: String
    ) : AddEditUiState()

    /**
     * État de succès après avoir inséré ou mis à jour un candidat.
     * @param message Le message de succès.
     */
    data class Success(val message: String) : AddEditUiState()

    /**
     * État d'erreur générique.
     * @param error Le message d'erreur.
     */
    data class Error(val error: String) : AddEditUiState()

    /**
     * État d'erreur spécifique lorsque des champs obligatoires sont vides.
     * On pourrait distinguer les différents champs plus tard, mais ici on affiche juste un message générique.
     */
    data class ErrorMandatoryFields(val error: String, val emptyFields: List<MandatoryField>) : AddEditUiState()

    /**
     * État d'erreur spécifique au format de l'email.
     * @param error Le message d'erreur.
     */
    data class ErrorEmailFormat(val error: String) : AddEditUiState()

    enum class MandatoryField {
        FIRST_NAME,
        LAST_NAME,
        PHONE,
        EMAIL,
        DATE_OF_BIRTH,
        EXPECTED_SALARY,
        NOTES
    }
}
