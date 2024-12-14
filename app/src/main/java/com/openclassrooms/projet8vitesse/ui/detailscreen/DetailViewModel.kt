package com.openclassrooms.projet8vitesse.ui.detailscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.projet8vitesse.domain.model.Candidate
import com.openclassrooms.projet8vitesse.domain.usecase.DeleteCandidateUseCase
import com.openclassrooms.projet8vitesse.domain.usecase.GetCandidateByIdUseCase
import com.openclassrooms.projet8vitesse.domain.usecase.GetConversionRateUseCase
import com.openclassrooms.projet8vitesse.domain.usecase.UpdateFavoriteStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import org.threeten.bp.temporal.ChronoUnit
import javax.inject.Inject

/**
 * ViewModel pour l'écran de détail d'un candidat.
 *
 * Ce ViewModel gère la logique de récupération du candidat via [GetCandidateByIdUseCase],
 * la mise à jour du statut de favori via [UpdateFavoriteStatusUseCase],
 * et la suppression du candidat via [DeleteCandidateUseCase].
 *
 * Le ViewModel expose un état (DetailUiState) que le Fragment observe
 * pour afficher ou non l'information, le chargement, les erreurs, etc.
 *
 * Les User Stories concernées par le DetailScreen incluent :
 * - Afficher la barre d'app avec le nom du candidat ("Prénom NOM")
 * - Afficher un bouton de retour
 * - Afficher une icône favori (étoile vide ou pleine) et permettre de basculer le statut
 * - Afficher une icône pour modifier le candidat (on navigue vers l'écran d'édition)
 * - Afficher une icône pour supprimer le candidat (avec un dialogue de confirmation)
 * - Afficher la photo du candidat
 * - Afficher la section "A propos" avec date de naissance formatée et âge
 * - Afficher la section "Prétentions salariales" avec conversion en livres (TODO : intégration de l'API)
 * - Afficher la section "Notes"
 * - Boutons pour Appel, SMS, Email permettant d'ouvrir les apps correspondantes
 */
@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getCandidateByIdUseCase: GetCandidateByIdUseCase,
    private val updateFavoriteStatusUseCase: UpdateFavoriteStatusUseCase,
    private val deleteCandidateUseCase: DeleteCandidateUseCase,
    private val getConversionRateUseCase: GetConversionRateUseCase
) : ViewModel() {

    // État de l'écran : Loading, Success(candidate), Error(message)
    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState

    // On stocke le candidat courant après l'avoir chargé pour pouvoir modifier son statut favori ou le supprimer
    private var currentCandidate: Candidate? = null

    /**
     * Charge un candidat par son ID.
     * Si le candidat existe, met à jour l'état avec [DetailUiState.Success].
     * Sinon, en cas d'erreur ou candidat introuvable, [DetailUiState.Error].
     *
     * @param candidateId L'ID du candidat à afficher
     * Charge un candidat par son ID et calcule le salaire en livres.
     */
    fun loadCandidate(candidateId: Long) {
        _uiState.value = DetailUiState.Loading
        viewModelScope.launch {
            getCandidateByIdUseCase.execute(candidateId)
                .catch { exception ->
                    _uiState.value = DetailUiState.Error(exception.message ?: "Erreur inconnue")
                }
                .collectLatest { candidate ->
                    if (candidate == null) {
                        _uiState.value = DetailUiState.Error("Candidat introuvable")
                    } else {
                        currentCandidate = candidate
                        val convertedSalary = try {
                            val rate = getConversionRateUseCase.execute()
                            val converted = candidate.expectedSalary * rate
                            String.format("soit £ %.2f", converted)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            // En cas de problème avec l'API, on met un fallback
                            "soit £ ??"
                        }

                        _uiState.value = DetailUiState.Success(candidate, convertedSalary)
                    }
                }
        }
    }

    /**
     * Basculer le statut de favori du candidat actuel.
     * Si le candidat est favori, il devient non-favori.
     * Si le candidat n'est pas favori, il devient favori.
     */
    fun toggleFavoriteStatus() {
        val candidate = currentCandidate ?: return
        viewModelScope.launch {
            try {
                val newStatus = !candidate.isFavorite
                updateFavoriteStatusUseCase.execute(candidate.id!!, newStatus)
                currentCandidate = candidate.copy(isFavorite = newStatus)
                // Recalculer le salaire converti si besoin (pas forcément nécessaire)
                val oldState = _uiState.value
                if (oldState is DetailUiState.Success) {
                    _uiState.value = oldState.copy(candidate = currentCandidate!!)
                }
            } catch (e: Exception) {
                _uiState.value = DetailUiState.Error("Impossible de changer le statut favori")
            }
        }
    }

    /**
     * Supprime le candidat actuel de la base.
     * Après suppression, le Fragment pourra naviguer vers l'écran d'accueil.
     *
     * Cette méthode est appelée après confirmation dans le Fragment.
     */
    fun deleteCurrentCandidate() {
        val candidate = currentCandidate ?: return
        viewModelScope.launch {
            try {
                deleteCandidateUseCase.execute(candidate)
                // Le fragment gère le retour en arrière
            } catch (e: Exception) {
                _uiState.value = DetailUiState.Error("Erreur lors de la suppression")
            }
        }
    }


    /**
     * Calcule l'âge du candidat à partir de sa date de naissance.
     *
     * @param dateOfBirth La date de naissance du candidat (Instant).
     * @return L'âge en années.
     */
    fun calculateAge(dateOfBirth: Instant): Int {
        val birthday = dateOfBirth.atZone(ZoneId.systemDefault()).toLocalDate()
        val now = LocalDate.now()
        return ChronoUnit.YEARS.between(birthday, now).toInt()
    }

    /**
     * Formatage de la date de naissance.
     * Ici, nous faisons simple : on affiche en format "dd/MM/yyyy".
     * En pratique, on pourrait adapter selon la locale.
     *
     * @param dateOfBirth La date de naissance (Instant).
     * @return Une chaîne de caractères formattée.
     */
    fun formatDateOfBirth(dateOfBirth: Instant): String {
        val zdt = dateOfBirth.atZone(ZoneId.systemDefault())
        val day = zdt.dayOfMonth.toString().padStart(2, '0')
        val month = zdt.monthValue.toString().padStart(2, '0')
        val year = zdt.year.toString()
        // Pour l'instant on respecte le format français "dd/MM/yyyy"
        return "$day/$month/$year"
    }

    /**
     * Conversion du salaire en livres.
     * Pour l'instant, on ne l'implémente pas, juste un TODO.
     *
     * TODO: Intégrer l'API pour convertir les euros en livres.
     * @param salaryInEuros Le salaire en euros.
     * @return La valeur convertie en livres (String formaté).
     */
    fun convertSalaryToPounds(salaryInEuros: Int): String {
        // TODO : Appeler l'API de conversion
        // Pour l'instant, on renvoie une valeur fictive.
        val fakeConversion = salaryInEuros * 0.86 // Juste pour illustrer, sans API réelle
        return String.format("soit £ %.2f", fakeConversion)
    }

}
