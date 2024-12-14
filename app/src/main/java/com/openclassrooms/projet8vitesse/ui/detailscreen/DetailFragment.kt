package com.openclassrooms.projet8vitesse.ui.detailscreen

import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.openclassrooms.projet8vitesse.R
import com.openclassrooms.projet8vitesse.databinding.FragmentDetailBinding
import com.openclassrooms.projet8vitesse.domain.model.Candidate
import com.openclassrooms.projet8vitesse.ui.addscreen.AddEditFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Fragment d'affichage du détail d'un candidat.
 *
 * Ce fragment :
 * - Observe [DetailViewModel] pour afficher l'état de l'UI ([DetailUiState]).
 * - Affiche le nom du candidat, sa photo, ses informations (âge, date de naissance, notes, etc.).
 * - Affiche le salaire en euros et en livres, grâce à la conversion API (obtenue via ViewModel).
 * - Permet de modifier le statut favori (étoile), de supprimer ou d'éditer le candidat.
 * - Permet d'appeler, envoyer un SMS ou un email au candidat.
 *
 * Les user stories :
 * - Affichage du nom "Prénom NOM" dans la toolbar.
 * - Navigation arrière avec la flèche.
 * - Icônes edit, delete, favori.
 * - Afficher date naissance formatée, âge, notes.
 * - Afficher salaire en euros et en livres (taux obtenu via API).
 * - Boutons appel, SMS, email pour contacter le candidat.
 */
@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailViewModel by viewModels()

    // ID du candidat affiché
    private var candidateId: Long = -1L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Méthode appelée lorsque la vue est prête.
     * On initialise la toolbar, on charge le candidat, et on observe le ViewModel.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        candidateId = arguments?.getLong("candidate_id", -1L) ?: -1L
        if (candidateId > 0) {
            viewModel.loadCandidate(candidateId)
        } else {
            Toast.makeText(requireContext(), "Candidat introuvable", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }

        setupToolbar()
        observeViewModel()
    }

    /**
     * Configure la barre d'application (toolbar).
     * - Icône retour
     * - Icône delete (suppression)
     * - Icône edit (édition)
     * - Icône favori (étoile)
     */
    private fun setupToolbar() {
        binding.topAppBar.setNavigationIcon(R.drawable.ic_back_arrow)
        binding.topAppBar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val deleteIcon = binding.topAppBar.findViewById<View>(R.id.delete_icon)
        val editIcon = binding.topAppBar.findViewById<View>(R.id.edit_icon)
        val favoriteIcon = binding.topAppBar.findViewById<View>(R.id.favorite_icon)

        // Suppression
        deleteIcon.setOnClickListener {
            showDeleteConfirmationDialog()
        }
        deleteIcon.setOnLongClickListener {
            Toast.makeText(requireContext(), getString(R.string.delete_confirmation), Toast.LENGTH_SHORT).show()
            true
        }

        // Édition
        editIcon.setOnClickListener {
            val fragment = AddEditFragment().apply {
                arguments = Bundle().apply {
                    putLong("candidate_id", candidateId)
                }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
        editIcon.setOnLongClickListener {
            Toast.makeText(requireContext(), getString(R.string.edit_icon), Toast.LENGTH_SHORT).show()
            true
        }

        // Favori
        favoriteIcon.setOnClickListener {
            viewModel.toggleFavoriteStatus()
        }
        favoriteIcon.setOnLongClickListener {
            Toast.makeText(requireContext(), getString(R.string.favorite_icon), Toast.LENGTH_SHORT).show()
            true
        }
    }

    /**
     * Observe les états du ViewModel pour mettre à jour l'UI.
     */
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    when (state) {
                        is DetailUiState.Loading -> {
                            showLoading(true)
                        }
                        is DetailUiState.Success -> {
                            showLoading(false)
                            // On a maintenant un convertedSalary dans l'état
                            updateUIWithCandidate(state.candidate, state.convertedSalary)
                        }
                        is DetailUiState.Error -> {
                            showLoading(false)
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    /**
     * Met à jour l'UI avec les informations du candidat et le salaire converti.
     *
     * @param candidate Le candidat chargé.
     * @param convertedSalary Le salaire converti au format "soit £ xxx.xx"
     */
    private fun updateUIWithCandidate(candidate: Candidate, convertedSalary: String) {
        // Nom du candidat dans la toolbar
        binding.topAppBar.title = "${candidate.firstName} ${candidate.lastName}"

        // Icône favori
        val favoriteIcon = binding.topAppBar.findViewById<android.widget.ImageView>(R.id.favorite_icon)
        if (candidate.isFavorite) {
            favoriteIcon.setImageResource(R.drawable.ic_star_filled)
        } else {
            favoriteIcon.setImageResource(R.drawable.ic_star_empty)
        }

        // Photo du candidat
        val placeholderBitmap = BitmapFactory.decodeResource(resources, R.drawable.media)
        binding.profilePhoto.setImageBitmap(candidate.photo ?: placeholderBitmap)

        // A propos : date de naissance et âge
        val formattedDate = viewModel.formatDateOfBirth(candidate.dateOfBirth)
        val age = viewModel.calculateAge(candidate.dateOfBirth)
        binding.tvFragmentDetailDateOfbirth.text = formattedDate
        binding.tvFragmentDetailAge.text = "$age ans"

        // Salaire
        binding.tvExpectedSalary.text = "${candidate.expectedSalary} €"
        // On a maintenant un TextView pour la conversion en livres, `tvConvertedSalary`
        binding.tvConvertedSalary.text = convertedSalary

        // Notes
        binding.tvDetailNotesToDisplay.text = candidate.note ?: ""

        // Boutons Appel, SMS, Email
        setupContactButtons(candidate)
    }

    /**
     * Configure les boutons de contact (Appel, SMS, Email).
     */
    private fun setupContactButtons(candidate: Candidate) {
        val contactBinding = binding.contact

        // Appel
        contactBinding.contactPhoneButton.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${candidate.phoneNumber}"))
            startActivity(dialIntent)
        }

        // SMS
        contactBinding.contactSmsButton.setOnClickListener {
            val smsIntent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:${candidate.phoneNumber}"))
            startActivity(smsIntent)
        }

        // Email
        contactBinding.contactEmailButton.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", candidate.email, null))
            startActivity(Intent.createChooser(emailIntent, "Envoyer un email..."))
        }
    }

    /**
     * Affiche ou cache le chargement.
     *
     * @param isLoading true pour afficher le ProgressBar, false pour afficher le contenu.
     */
    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.scrollView.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.scrollView.visibility = View.VISIBLE
        }
    }

    /**
     * Affiche une boîte de dialogue pour confirmer la suppression du candidat.
     */
    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_confirmation_title))
            .setMessage(getString(R.string.delete_confirmation_message))
            .setPositiveButton(getString(R.string.delete_confirmation)) { dialog, _ ->
                viewModel.deleteCurrentCandidate()
                dialog.dismiss()
                // Retour à l'accueil
                parentFragmentManager.popBackStack()
            }
            .setNegativeButton(getString(R.string.delete_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
