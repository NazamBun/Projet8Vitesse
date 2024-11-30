package com.openclassrooms.projet8vitesse.ui.homescrreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.projet8vitesse.data.repository.CandidateRepository
import com.openclassrooms.projet8vitesse.utils.FilterType
import com.openclassrooms.projet8vitesse.utils.toCandidateList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel pour gérer les données et la logique de la HomeScreen.
 * Ce ViewModel gère les candidats et les filtre en fonction du type (tous ou favoris).
 *
 * @property repository Le repository pour accéder aux données des candidats.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: CandidateRepository) : ViewModel() {

    /**
     * État de chargement des données.
     */
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    /**
     * État vide (aucun candidat à afficher).
     */
    private val _isEmpty = MutableStateFlow(false)
    val isEmpty: StateFlow<Boolean> get() = _isEmpty

    /**
     * État du filtre actuel (tous ou favoris).
     */
    private val _filter = MutableStateFlow(FilterType.ALL)
    val filter: StateFlow<FilterType> = _filter

    /**
     * Liste des candidats filtrés en fonction du filtre actuel.
     */
    val filteredCandidates = _filter.flatMapLatest { filterType ->
        when (filterType) {
            FilterType.ALL -> repository.getAllCandidates()
            FilterType.FAVORITES -> repository.getFavoriteCandidates()
        }.map { entities ->
            _isLoading.value = false
            _isEmpty.value = entities.isEmpty()
            entities.toCandidateList() // Mapper les entités vers les modèles métier
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    /**
     * Charge les données des candidats depuis le repository.
     */
    fun loadCandidates() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getAllCandidates().collect { candidates ->
                _isLoading.value = false
                _isEmpty.value = candidates.isEmpty()
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
}
