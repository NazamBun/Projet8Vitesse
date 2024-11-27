package com.openclassrooms.projet8vitesse.ui.homescrreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.projet8vitesse.data.local.entities.CandidateEntity
import com.openclassrooms.projet8vitesse.repository.CandidateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// ViewModel pour gérer les données et la logique de HomeScreen
@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: CandidateRepository): ViewModel() {

    // État pour tous les candidats
    private val _allCandidates = MutableStateFlow<List<CandidateEntity>>(emptyList())
    val allCandidates: StateFlow<List<CandidateEntity>> = _allCandidates

    // État pour les candidats favoris
    private val _favoriteCandidates = MutableStateFlow<List<CandidateEntity>>(emptyList())
    val favoriteCandidates: StateFlow<List<CandidateEntity>> = _favoriteCandidates

    // Charger les candidats depuis le repository
    fun loadCandidates() {
        viewModelScope.launch {
            repository.getAllCandidates().collect { candidates ->
                _allCandidates.value = candidates
            }
        }
    }

    // Charger les favoris depuis le repository
    fun loadFavoritesCandidates() {
        viewModelScope.launch {
            repository.getFavoriteCandidates().collect { candidates ->
                _favoriteCandidates.value = candidates
            }
        }
    }
}