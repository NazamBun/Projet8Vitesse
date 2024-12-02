package com.openclassrooms.projet8vitesse.ui.homescrreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.projet8vitesse.data.repository.CandidateRepository
import com.openclassrooms.projet8vitesse.domain.model.Candidate
import com.openclassrooms.projet8vitesse.domain.usecase.GetCandidatesUseCase
import com.openclassrooms.projet8vitesse.domain.usecase.UpdateFavoriteStatusUseCase
import com.openclassrooms.projet8vitesse.utils.FilterType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * ViewModel pour gérer l'état de l'écran d'accueil (HomeScreen).
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCandidateUseCase: GetCandidatesUseCase,
    private val updateFavoriteStatusUseCase: UpdateFavoriteStatusUseCase
) : ViewModel() {

    // État de l'écran d'accueil
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    // Derniers filtres appliqués
    private var currentFilter: String? = null
    private var showFavoritesOnly: Boolean = false

    /**
     * Charge la liste des candidats selon les filtres actuels.
     */
    fun loadCandidates(filter: String? = null, favoritesOnly: Boolean = false) {
        currentFilter = filter
        showFavoritesOnly = favoritesOnly

        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            getCandidateUseCase.execute(
                favorite = if (favoritesOnly) true else null,
                name = filter
            ).catch { exception ->
                _uiState.value = HomeUiState.Error(exception.message ?: "Unknown error")
            }.collectLatest { candidates ->
                _uiState.value = if (candidates.isEmpty()) {
                    HomeUiState.Empty
                } else {
                    HomeUiState.Success(candidates)
                }
            }
        }
    }
    /**
     * Met à jour le statut de favori d'un candidat.
     * @param candidate Le candidat dont le statut doit être mis à jour.
     */
    fun updateFavoriteStatus(candidate: Candidate) {
        viewModelScope.launch {
            try {
                val newStatus = !candidate.isFavorite
                updateFavoriteStatusUseCase.execute(candidate.id!!, newStatus)
                // Recharger les candidats après modification
                loadCandidates(currentFilter, showFavoritesOnly)
            } catch (exception: Exception) {
                _uiState.value = HomeUiState.Error("Failed to update favorite status")
            }
        }
    }
}


