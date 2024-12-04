package com.openclassrooms.projet8vitesse.ui.detailscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import com.openclassrooms.projet8vitesse.R
import com.openclassrooms.projet8vitesse.databinding.FragmentDetailBinding
import com.openclassrooms.projet8vitesse.domain.model.Candidate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Fragment pour afficher les détails d'un candidat.
 */
@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Récupérer l'ID du candidat depuis les arguments
        val candidateId = arguments?.getLong(ARG_CANDIDATE_ID)
            ?: throw IllegalArgumentException("Candidate ID is required for DetailFragment")

        // Charger les détails du candidat
        viewModel.loadCandidate(candidateId)

        setupTopAppBar()
        observeViewModel()
    }

    /**
     * Configure la TopAppBar avec les icônes et les actions nécessaires.
     */
    private fun setupTopAppBar() {
        val toolbar: MaterialToolbar = binding.topAppBar

        // Action pour la flèche de navigation
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Action pour le bouton Favori
        binding.favoriteIcon.setOnClickListener {
            viewModel.toggleFavoriteStatus()
        }

        // Action pour le bouton Modifier
        binding.editIcon.setOnClickListener {
            Toast.makeText(requireContext(), "Navigate to Edit Screen", Toast.LENGTH_SHORT).show()
            // Implémenter la navigation vers EditFragment
        }

        // Action pour le bouton Supprimer
        binding.deleteIcon.setOnClickListener {
            Toast.makeText(requireContext(), "Candidate deleted", Toast.LENGTH_SHORT).show()
            viewModel.deleteCandidate()
        }
    }

    /**
     * Observe les données du ViewModel pour afficher les informations du candidat.
     */
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.candidate.collectLatest { candidate ->
                candidate?.let {
                    updateUI(it)
                }
            }
        }
    }

    /**
     * Met à jour l'interface utilisateur avec les données du candidat.
     * @param candidate Le candidat actuellement sélectionné.
     */
    private fun updateUI(candidate: Candidate) {
        // Mettre à jour le titre avec le nom complet
        binding.topAppBar.title = "${candidate.firstName} ${candidate.lastName.uppercase()}"

        // Mettre à jour l'icône Favori
        binding.favoriteIcon.setImageResource(
            if (candidate.isFavorite) R.drawable.ic_star_filled else R.drawable.ic_star_empty
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object {
        private const val ARG_CANDIDATE_ID = "candidate_id"

        /**
         * Crée une instance de DetailFragment avec l'ID du candidat.
         * @param candidateId L'ID du candidat à afficher.
         * @return Une nouvelle instance configurée de DetailFragment.
         */
        fun newInstance(candidateId: Long): DetailFragment {
            return DetailFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_CANDIDATE_ID, candidateId)
                }
            }
        }
    }
}
