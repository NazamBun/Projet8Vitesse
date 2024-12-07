package com.openclassrooms.projet8vitesse.ui.homescrreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.openclassrooms.projet8vitesse.R
import com.openclassrooms.projet8vitesse.databinding.FragmentHomeBinding
import com.openclassrooms.projet8vitesse.domain.model.Candidate
import com.openclassrooms.projet8vitesse.ui.homescrreen.adapter.CandidateAdapter
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

        // Charger les candidats initiaux
        viewModel.loadCandidates() // Charger les candidats initiaux

        // Écouter les résultats de l'ajout/modification d'un candidat
        listenToAddCandidateResult()

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
                viewModel.loadCandidates(favoritesOnly = showFavorites)
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
            viewModel.loadCandidates(filter = query)
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
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is HomeUiState.Loading -> showLoadingState()
                        is HomeUiState.Success -> showCandidates(state.candidates)
                        is HomeUiState.Empty -> showEmptyState()
                        is HomeUiState.Error -> showError(state.message)
                    }
                }
            }
        }
    }

    /**
     * Écoute les résultats envoyés par `AddEditFragment` pour recharger la liste.
     */
    private fun listenToAddCandidateResult() {
        setFragmentResultListener("add_candidate_request") { _, bundle ->
            val candidateAdded = bundle.getBoolean("candidate_added", false)
            if (candidateAdded) {
                viewModel.reloadCandidates() // Recharger la liste des candidats
                Toast.makeText(
                    requireContext(),
                    getString(R.string.candidate_added_successfully),
                    Toast.LENGTH_SHORT
                ).show()
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
        _binding?.let {
            binding.progressBar.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
            binding.emptyStateText.visibility = View.GONE
            candidateAdapter.submitList(candidates)
        }
    }

    /**
     * Affiche un message d'état vide si aucun candidat n'est disponible.
     */
    private fun showEmptyState() {
        _binding?.let {
            binding.progressBar.visibility = View.GONE
            binding.recyclerView.visibility = View.GONE
            binding.emptyStateText.visibility = View.VISIBLE
        }
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