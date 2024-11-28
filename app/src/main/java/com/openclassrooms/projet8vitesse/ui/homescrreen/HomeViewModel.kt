package com.openclassrooms.projet8vitesse.ui.homescrreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.projet8vitesse.data.local.entities.CandidateEntity
import com.openclassrooms.projet8vitesse.repository.CandidateRepository
import com.openclassrooms.projet8vitesse.utils.FilterType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel pour gérer les données et la logique de la HomeScreen.
 * Ce ViewModel gère les candidats et les filtre en fonction du type (tous ou favoris).
 *
 * @property repository Le repository pour accéder aux données des candidats.
 */@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: CandidateRepository): ViewModel() {

    /**
     * État du filtre actuel (tous ou favoris).
     */
    private val _filter = MutableStateFlow(FilterType.ALL)
    val filter: StateFlow<FilterType> = _filter

    /**
     * Liste des candidats filtrés en fonction de l'état du filtre.
     * - `FilterType.ALL` : Tous les candidats.
     * - `FilterType.FAVORITES` : Candidats favoris uniquement.
     */
    val filteredCandidates = _filter.flatMapLatest { filterType ->
        when (filterType) {
            FilterType.ALL -> repository.getAllCandidates()
            FilterType.FAVORITES -> repository.getFavoriteCandidates()
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = emptyList()
    )

    /**
     * Liste de tous les candidats récupérés depuis le repository.
     */
    private val _allCandidates = MutableStateFlow<List<CandidateEntity>>(emptyList())
    val allCandidates: StateFlow<List<CandidateEntity>> = _allCandidates

    /**
     * Liste des candidats favoris récupérés depuis le repository.
     */
    private val _favoriteCandidates = MutableStateFlow<List<CandidateEntity>>(emptyList())
    val favoriteCandidates: StateFlow<List<CandidateEntity>> = _favoriteCandidates

    /**
     * Charge tous les candidats depuis le repository.
     * Les données sont collectées et stockées dans `_allCandidates`.
     */
    fun loadCandidates() {
        viewModelScope.launch {
            repository.getAllCandidates().collect { candidates ->
                _allCandidates.value = candidates
            }
        }
    }

    /**
     * Change le filtre appliqué pour la liste des candidats.
     *
     * @param filterType Le type de filtre à appliquer (tous ou favoris).
     */
    fun setFilter(filterType: FilterType) {
        _filter.value = filterType
    }

    /**
     * Charge les candidats favoris depuis le repository.
     * Les données sont collectées et stockées dans `_favoriteCandidates`.
     */
    fun loadFavoritesCandidates() {
        viewModelScope.launch {
            repository.getFavoriteCandidates().collect { candidates ->
                _favoriteCandidates.value = candidates
            }
        }
    }
}