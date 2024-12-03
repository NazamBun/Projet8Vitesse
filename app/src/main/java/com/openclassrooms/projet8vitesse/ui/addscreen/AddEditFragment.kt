package com.openclassrooms.projet8vitesse.ui.addscreen

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.openclassrooms.projet8vitesse.R
import com.openclassrooms.projet8vitesse.databinding.FragmentAddEditBinding
import com.openclassrooms.projet8vitesse.domain.model.Candidate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.Instant

@AndroidEntryPoint
class AddEditFragment : Fragment() {

    private var _binding: FragmentAddEditBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddEditViewModel by viewModels()

    private var candidatePhoto: Bitmap? = null // Champ pour stocker la photo du candidat

    // Gestionnaire de résultats pour sélectionner une image
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                val inputStream = requireContext().contentResolver.openInputStream(uri)
                candidatePhoto = BitmapFactory.decodeStream(inputStream)
                binding.candidatePhoto.setImageBitmap(candidatePhoto)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTopAppBar()
        observeUiState()
        setupSaveButton()
        setupPhotoClick()
    }

    /**
     * Configure la TopAppBar avec un titre et une icône de navigation.
     */
    private fun setupTopAppBar() {
        binding.topAppBar.title = getString(R.string.add_candidate)
        binding.topAppBar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    /**
     * Configure l'action du clic sur la photo pour ouvrir la galerie.
     */
    private fun setupPhotoClick() {
        binding.candidatePhoto.setOnClickListener {
            pickImageLauncher.launch("image/*") // Lancer la sélection d'image
        }
    }

    /**
     * Observe l'état de l'interface utilisateur via le ViewModel.
     *
     * Utilise repeatOnLifecycle pour éviter les pertes de ressources.
     */
    private fun observeUiState() {
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is AddEditUiState.Loading -> showLoading()
                        is AddEditUiState.Success -> showSuccess(state.message)
                        is AddEditUiState.Error -> showError(state.error)
                        AddEditUiState.Idle -> Unit // Aucun changement
                    }
                }
            }
        }
    }

    /**
     * Configure l'action du bouton "Save".
     */
    private fun setupSaveButton() {
        binding.saveButton.setOnClickListener {
            val candidate = gatherCandidateData()
            if (candidate != null) {
                viewModel.insertCandidate(candidate)
            } else {
                Toast.makeText(requireContext(), R.string.missing_fields_error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Rassemble les données saisies dans le formulaire pour créer un objet Candidate.
     * @return Un objet Candidate si toutes les données sont valides, sinon null.
     */
    private fun gatherCandidateData(): Candidate? {
        val firstName = binding.tiFirstname.text.toString()
        val lastName = binding.tiLastname.text.toString()
        val phoneNumber = binding.tiPhone.text.toString()
        val email = binding.tiEmail.text.toString()
        val note = binding.tiNotes.text.toString()
        val salary = binding.tiSalary.text.toString().toIntOrNull()
        val dateOfBirth = Instant.now() // Remplacez par une date valide sélectionnée par l'utilisateur

        return if (firstName.isNotEmpty() && lastName.isNotEmpty() && phoneNumber.isNotEmpty() && email.isNotEmpty() && salary != null && candidatePhoto != null) {
            Candidate(
                firstName = firstName,
                lastName = lastName,
                photo = candidatePhoto!!,
                phoneNumber = phoneNumber,
                email = email,
                dateOfBirth = dateOfBirth,
                expectedSalary = salary,
                note = note,
                isFavorite = false
            )
        } else {
            null
        }
    }

    /**
     * Affiche l'état de chargement dans l'interface utilisateur.
     */
    private fun showLoading() {
        // Afficher une ProgressBar ou tout autre indicateur visuel
    }

    /**
     * Affiche un message de succès.
     * @param message Le message à afficher.
     */
    private fun showSuccess(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    /**
     * Affiche un message d'erreur.
     * @param error Le message d'erreur à afficher.
     */
    private fun showError(error: String) {
        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
