package com.openclassrooms.projet8vitesse.ui.detailscreen

import android.graphics.BitmapFactory
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
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Fragment pour afficher les détails d'un candidat.
 *
 * Cette classe :
 * - Récupère l'ID du candidat dans les arguments.
 * - Charge le candidat via le ViewModel.
 * - Observe le ViewModel pour réagir aux changements (affichage du candidat, navigation, suppression).
 * - Met à jour l'UI (photo, nom, âge, etc.) en fonction du candidat obtenu.
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

    /**
     * Appelée après la création de la vue.
     * On récupère l'ID du candidat, on charge les données, on met en place la toolbar et on observe le ViewModel.
     */
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
     * Observe le ViewModel pour :
     * - Afficher le candidat quand il est disponible.
     * - Réagir aux demandes de navigation (édition, retour après suppression).
     */
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.candidate.collectLatest { candidate ->
                candidate?.let {
                    updateUI(it)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.navigateToEdit.collectLatest { navigate ->
                if (navigate) {
                    Toast.makeText(requireContext(), "Navigate to Edit Screen", Toast.LENGTH_SHORT).show()
                    // Implémenter la navigation vers EditFragment ici
                    viewModel.resetNavigationFlags()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.navigateBackAfterDelete.collectLatest { navigate ->
                if (navigate) {
                    // On revient en arrière après la suppression
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                    viewModel.resetNavigationFlags()
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

        // Mettre à jour la photo du candidat
        val photoToDisplay = candidate.photo ?: BitmapFactory.decodeResource(
            resources,
            R.drawable.media // Placeholder par défaut si aucune photo n'est disponible
        )
        binding.profilePhoto.setImageBitmap(photoToDisplay)

        // Formate et affiche la date de naissance
        val formattedDate = formatDateOfBirth(candidate.dateOfBirth)
        binding.tvFragmentDetailDateOfbirth.text = formattedDate

        // Calcule et affiche l'âge
        val age = calculateAge(candidate.dateOfBirth)
        binding.tvFragmentDetailAge.text = getString(R.string.age_format, age)

        val salaryText = "${candidate.expectedSalary} €"
        binding.tvExpectedSalary.text = salaryText

        val notesToDisplay = candidate.note ?: ""
        binding.tvDetailNotesToDisplay.text = notesToDisplay
    }

    /**
     * Formate une date de naissance en chaîne lisible.
     * @param dateOfBirth La date de naissance en Instant.
     * @return La date formatée en String.
     */
    private fun formatDateOfBirth(dateOfBirth: Instant): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return formatter.format(Date.from(dateOfBirth))
    }

    /**
     * Calcule l'âge à partir de la date de naissance.
     * @param dateOfBirth La date de naissance du candidat en Instant.
     * @return L'âge en années.
     */
    private fun calculateAge(dateOfBirth: Instant): Long {
        val birthDate = dateOfBirth.atZone(ZoneId.systemDefault()).toLocalDate()
        val currentDate = LocalDate.now()
        return ChronoUnit.YEARS.between(birthDate, currentDate)
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
