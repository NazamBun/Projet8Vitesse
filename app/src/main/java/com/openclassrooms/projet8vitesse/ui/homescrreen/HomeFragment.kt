package com.openclassrooms.projet8vitesse.ui.homescrreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.openclassrooms.projet8vitesse.R
import com.openclassrooms.projet8vitesse.databinding.FragmentHomeBinding
import com.openclassrooms.projet8vitesse.domain.model.Candidate
import com.openclassrooms.projet8vitesse.presentation.ui.homescreen.adapter.CandidateAdapter
import com.openclassrooms.projet8vitesse.ui.MainActivity
import com.openclassrooms.projet8vitesse.ui.detailscreen.DetailFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Fragment principal affichant l'écran d'accueil avec la liste des candidats.
 */
@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var candidateAdapter: CandidateAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupTabLayout()
        setupSearchBar()
        setupFloatingActionButton()
        observeViewModel()
        homeViewModel.loadCandidates() // Charger les candidats initiaux

    }

    /**
     * Configure le RecyclerView et son adapter.
     */
    private fun setupRecyclerView() {
        candidateAdapter = CandidateAdapter { candidate ->
            onCandidateClicked(candidate)
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = candidateAdapter
        }
    }

    /**
     * Configure le TabLayout pour les onglets "Tous" et "Favoris".
     */
    private fun setupTabLayout() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.all_candidates))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.favorite_candidates))

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val showFavorites = tab?.position == 1
                homeViewModel.loadCandidates(favoritesOnly = showFavorites)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    /**
     * Configure la barre de recherche pour filtrer les candidats.
     */
    private fun setupSearchBar() {
        binding.searchEditText.setOnEditorActionListener { _, _, _ ->
            val query = binding.searchEditText.text.toString()
            homeViewModel.loadCandidates(filter = query)
            true
        }
    }

    /**
     * Configure le Floating Action Button pour ajouter un candidat.
     */
    private fun setupFloatingActionButton() {
        binding.fabAddCandidate.setOnClickListener {
            (requireActivity() as MainActivity).navigateToAddEdit()
        }
    }

    /**
     * Observe les changements d'état dans le ViewModel.
     */
    private fun observeViewModel() {
        lifecycleScope.launch {
            homeViewModel.uiState.collect { state ->
                when (state) {
                    is HomeUiState.Loading -> showLoadingState()
                    is HomeUiState.Success -> showCandidates(state.candidates)
                    is HomeUiState.Empty -> showEmptyState()
                    is HomeUiState.Error -> showError(state.message)
                }
            }
        }
    }

    /**
     * Affiche l'état de chargement.
     */
    private fun showLoadingState() {
        binding.progressBar.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
        binding.emptyStateText.visibility = View.GONE
    }

    /**
     * Affiche les candidats dans le RecyclerView.
     */
    private fun showCandidates(candidates: List<Candidate>) {
        binding.progressBar.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
        binding.emptyStateText.visibility = View.GONE
        candidateAdapter.submitList(candidates)
    }

    /**
     * Affiche un message d'état vide si aucun candidat n'est disponible.
     */
    private fun showEmptyState() {
        binding.progressBar.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.emptyStateText.visibility = View.VISIBLE
    }

    /**
     * Affiche un message d'erreur.
     * @param message Le message d'erreur à afficher.
     */
    private fun showError(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.emptyStateText.visibility = View.GONE
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Action lors du clic sur un candidat.
     * @param candidate Le candidat sélectionné.
     */
    private fun onCandidateClicked(candidate: Candidate) {
        val fragment = DetailFragment.newInstance(candidate.id ?: 0)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}