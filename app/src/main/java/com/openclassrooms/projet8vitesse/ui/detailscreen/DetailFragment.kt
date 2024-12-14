package com.openclassrooms.projet8vitesse.ui.detailscreen

import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.telephony.SmsManager
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
 * Fragment pour afficher les détails d'un candidat.
 *
 * Ce Fragment :
 * - Observe le ViewModel (DetailViewModel) et met à jour l'UI selon DetailUiState.
 * - Affiche le candidat (photo, nom, infos, notes, etc.).
 * - Permet de toggler le favori, de supprimer le candidat, d’éditer le candidat.
 * - Permet d’appeler, envoyer SMS, envoyer Email au candidat.
 * - Gère la barre d’application (flèche retour, icônes).
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
     * Appelé quand la vue est prête.
     * On initialise l'UI et on observe le ViewModel.
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
     * Configure la barre d’application :
     * - Flèche pour revenir en arrière
     * - Icône favori
     * - Icône edit
     * - Icône delete
     */
    private fun setupToolbar() {
        binding.topAppBar.setNavigationIcon(R.drawable.ic_back_arrow)
        binding.topAppBar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Les icônes sont déjà dans le XML
        // On les récupère par ID
        val deleteIcon = binding.topAppBar.findViewById<View>(R.id.delete_icon)
        val editIcon = binding.topAppBar.findViewById<View>(R.id.edit_icon)
        val favoriteIcon = binding.topAppBar.findViewById<View>(R.id.favorite_icon)

        deleteIcon.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        // Long press sur deleteIcon pour afficher le texte alternatif
        deleteIcon.setOnLongClickListener {
            Toast.makeText(requireContext(), getString(R.string.delete_confirmation), Toast.LENGTH_SHORT).show()
            true
        }

        editIcon.setOnClickListener {
            // Naviguer vers AddEditFragment en mode édition
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

        favoriteIcon.setOnClickListener {
            viewModel.toggleFavoriteStatus()
        }

        favoriteIcon.setOnLongClickListener {
            Toast.makeText(requireContext(), getString(R.string.favorite_icon), Toast.LENGTH_SHORT).show()
            true
        }
    }

    /**
     * Observe le ViewModel (uiState) et met à jour l'UI.
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
                            updateUIWithCandidate(state.candidate)
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
     * Met à jour l’UI avec les informations du candidat.
     */
    private fun updateUIWithCandidate(candidate: Candidate) {
        // Mettre à jour le titre de la barre d'app : "Prénom NOM"
        binding.topAppBar.title = "${candidate.firstName} ${candidate.lastName}"

        // Gérer l'icône favori (étoile)
        val favoriteIcon = binding.topAppBar.findViewById<View>(R.id.favorite_icon)
        if (candidate.isFavorite) {
            (favoriteIcon as? android.widget.ImageView)?.setImageResource(R.drawable.ic_star_filled)
        } else {
            (favoriteIcon as? android.widget.ImageView)?.setImageResource(R.drawable.ic_star_empty)
        }

        // Afficher la photo
        val placeholderBitmap = BitmapFactory.decodeResource(resources, R.drawable.media)
        binding.profilePhoto.setImageBitmap(candidate.photo ?: placeholderBitmap)

        // Section "A propos" : date de naissance formatée et âge
        val formattedDate = viewModel.formatDateOfBirth(candidate.dateOfBirth)
        val age = viewModel.calculateAge(candidate.dateOfBirth)
        binding.tvFragmentDetailDateOfbirth.text = formattedDate
        binding.tvFragmentDetailAge.text = "$age ans"
        // Titre "A propos" déjà dans le layout
        // "Anniversaire" déjà en dur, on pourrait l'adapter si besoin

        // Section "Prétentions salariales"
        binding.tvExpectedSalary.text = "${candidate.expectedSalary} €"
        // Conversion en livres (temporaire)
        val convertedSalary = viewModel.convertSalaryToPounds(candidate.expectedSalary)
        // On affiche : soit £ xxx.xx
        // On l’affiche sous le salaire en euros
        // Le texte est déjà dans le layout, on le met à jour
        // La CardView a un TextView supplémentaire pour l'afficher
        // Ici on va simplement retrouver ce TextView et le mettre à jour
        val salaryLayout = binding.cvExpectedSalary
        // Pour simplifier, on prend le second TextView (tv_expected_salary est déjà le premier)
        // On suppose que le deuxième TextView dans la card est celui qui affiche la conversion
        // Ou alors, si on a un id, ce serait mieux. Ajoutons un id si besoin :
        // On assume qu'il existe un TextView sans id, on va lui attribuer un id dans le layout pour plus de clarté.
        // Supposons qu'on lui donne android:id="@+id/tv_converted_salary" dans le layout
        binding.tvConvertedSalary.text = convertedSalary


        // Section "Notes"
        binding.tvDetailNotesToDisplay.text = candidate.note ?: ""

        // Section Contact (Appel, SMS, Email)
        setupContactButtons(candidate)
    }

    /**
     * Configure les boutons Appel, SMS, Email avec le view binding.
     */
    private fun setupContactButtons(candidate: Candidate) {
        // Ici, binding.contact est déjà un objet binding pour le layout inclus,
        // donc on accède directement aux vues sans findViewById.
        val contactBinding = binding.contact

        // Bouton Appel : ouvre le dialer avec le numéro
        contactBinding.contactPhoneButton.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${candidate.phoneNumber}"))
            startActivity(dialIntent)
        }

        // Bouton SMS : ouvre l'app SMS avec le numéro
        contactBinding.contactSmsButton.setOnClickListener {
            val smsIntent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:${candidate.phoneNumber}"))
            startActivity(smsIntent)
        }

        // Bouton Email : ouvre l'app email avec l'adresse pré-remplie
        contactBinding.contactEmailButton.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", candidate.email, null))
            startActivity(Intent.createChooser(emailIntent, "Envoyer un email..."))
        }
    }


    /**
     * Affiche ou cache le ProgressBar et le contenu.
     */
    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            // On masque le reste
            binding.scrollView.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.scrollView.visibility = View.VISIBLE
        }
    }

    /**
     * Affiche une boîte de dialogue de confirmation pour supprimer le candidat.
     */
    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_confirmation_title))
            .setMessage(getString(R.string.delete_confirmation_message))
            .setPositiveButton(getString(R.string.delete_confirmation)) { dialog, _ ->
                viewModel.deleteCurrentCandidate()
                dialog.dismiss()
                // On revient à l'accueil après suppression
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
