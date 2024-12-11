package com.openclassrooms.projet8vitesse.ui.addscreen

import android.app.DatePickerDialog
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Patterns
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
import com.openclassrooms.projet8vitesse.ui.detailscreen.DetailFragment
import com.openclassrooms.projet8vitesse.utils.DateUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.threeten.bp.Instant
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Fragment permettant d'ajouter ou modifier un candidat.
 *
 * Ce fragment gère :
 * - La sélection de la photo
 * - La saisie des informations du candidat
 * - La validation des champs
 * - L'enregistrement du candidat via le ViewModel
 * - La navigation vers l'écran de détail en cas de succès
 */
@AndroidEntryPoint
class AddEditFragment : Fragment() {

    // Liaison au layout du fragment
    private var _binding: FragmentAddEditBinding? = null
    private val binding get() = _binding!!

    // ViewModel pour gérer la logique métier
    private val viewModel: AddEditViewModel by viewModels()

    /**
     * Lanceur d'activité pour sélectionner une image dans la galerie.
     * Quand l'utilisateur sélectionne une image, on la met à jour dans le ViewModel,
     * puis on l'affiche directement dans l'ImageView.
     */
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                val inputStream = requireContext().contentResolver.openInputStream(uri)
                val bitmap = inputStream?.use { BitmapFactory.decodeStream(it) }

                if (bitmap != null) {
                    // Mettre à jour la photo dans le ViewModel
                    viewModel.updatePhoto(bitmap)
                    // Afficher immédiatement la photo sélectionnée
                    binding.candidatePhoto.setImageBitmap(bitmap)
                }
            }
        }

    // ID du candidat, si on est en mode édition, sinon -1
    private var candidateId: Long = -1

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
        setupDateOfBirthPicker()
        setupMode()
    }

    private fun setupMode() {
        candidateId = arguments?.getLong(ARG_CANDIDATE_ID, -1) ?: -1
        if (candidateId != -1L) {
            // Mode édition
            binding.topAppBar.title = getString(R.string.edit_candidate)
            viewModel.loadCandidateForEdit(candidateId)
            observeCandidateData()
        } else {
            // Mode ajout
            binding.topAppBar.title = getString(R.string.add_candidate)
        }
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
     * Observe candidateData du ViewModel pour pré-remplir les champs lorsqu'on est en mode édition.
     */
    private fun observeCandidateData() {
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.candidateDataFlow.collect { candidate ->
                    // Pré-remplir les champs avec les données du candidat
                    binding.tiFirstname.setText(candidate.firstName)
                    binding.tiLastname.setText(candidate.lastName)
                    binding.tiPhone.setText(candidate.phoneNumber)
                    binding.tiEmail.setText(candidate.email)
                    binding.tiSalary.setText(candidate.expectedSalary.toString())
                    binding.tiNotes.setText(candidate.note ?: "")

                    // Photo si disponible
                    candidate.photo?.let {
                        binding.candidatePhoto.setImageBitmap(it)
                    }

                    // Date de naissance
                    if (candidate.dateOfBirth != Instant.EPOCH) {
                        val formattedDate = DateUtils.localeDateTimeStringFromInstant(candidate.dateOfBirth)
                        binding.tieAddEditDateOfBirth.setText(formattedDate)
                    }
                }
            }
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
     * Configure le champ de sélection de la date de naissance avec un DatePickerDialog.
     */
    private fun setupDateOfBirthPicker() {
        binding.tieAddEditDateOfBirth.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, month, dayOfMonth)
                    viewModel.updateDateOfBirth(DateUtils.computeInstantFromLocalDate(year,month,dayOfMonth))

                    // Formater la date et l’afficher dans le champ
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val formattedDate = dateFormat.format(selectedDate.time)
                    binding.tieAddEditDateOfBirth.setText(formattedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.datePicker.maxDate = System.currentTimeMillis()
            datePicker.show()
        }
    }

    /**
     * Configure l'action du bouton "Save".
     */
    private fun setupSaveButton() {
        binding.saveButton.setOnClickListener {
            if (validateFields()) {
                // Récupérer les données depuis les champs
                val firstName = binding.tiFirstname.text?.toString()?.trim() ?: ""
                val lastName = binding.tiLastname.text?.toString()?.trim() ?: ""
                val phoneNumber = binding.tiPhone.text?.toString()?.trim() ?: ""
                val email = binding.tiEmail.text?.toString()?.trim() ?: ""
                val dateOfBirth = binding.tieAddEditDateOfBirth.text?.toString()?.trim() ?: ""
                val salary = binding.tiSalary.text?.toString()?.trim() ?: "0"
                val notes = binding.tiNotes.text?.toString()?.trim() ?: ""

                // Mettre à jour les données du candidat dans le ViewModel
                viewModel.updateCandidateData(
                    firstName = firstName,
                    lastName = lastName,
                    phoneNumber = phoneNumber,
                    email = email,
                    expectedSalary = salary.toInt(),
                    notes = notes
                )

                // Appeler la fonction pour sauvegarder le candidat
                viewModel.onSaveCandidate()
            }
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
                        is AddEditUiState.Success -> showSuccess(state.message, state.candidateId)
                        is AddEditUiState.Error -> showError(state.error)
                        AddEditUiState.Idle -> Unit // Aucun changement
                    }
                }
            }
        }
    }

    /**
     * Validation des champs obligatoires et du format de l'email.
     * Retourne true si tous les champs sont remplis correctement, sinon false.
     */
    private fun validateFields(): Boolean {
        var allValid = true

        // Récupération des valeurs saisies
        val firstName = binding.tiFirstname.text?.toString()?.trim() ?: ""
        val lastName = binding.tiLastname.text?.toString()?.trim() ?: ""
        val phoneNumber = binding.tiPhone.text?.toString()?.trim() ?: ""
        val email = binding.tiEmail.text?.toString()?.trim() ?: ""
        val dateOfBirth = binding.tieAddEditDateOfBirth.text?.toString()?.trim() ?: ""
        val salary = binding.tiSalary.text?.toString()?.trim() ?: ""
        val notes = binding.tiNotes.text?.toString()?.trim() ?: ""

        // Réinitialisation des erreurs
        binding.tilFirstname.error = null
        binding.tilLastname.error = null
        binding.tilPhone.error = null
        binding.tilEmail.error = null
        binding.tilAddEditDateOfBirth.error = null
        binding.tilSalary.error = null
        binding.tilNotes.error = null

        // Vérification des champs obligatoires
        if (firstName.isEmpty()) {
            binding.tilFirstname.error = getString(R.string.missing_fields_error)
            allValid = false
        }

        if (lastName.isEmpty()) {
            binding.tilLastname.error = getString(R.string.missing_fields_error)
            allValid = false
        }

        if (phoneNumber.isEmpty()) {
            binding.tilPhone.error = getString(R.string.missing_fields_error)
            allValid = false
        }

        if (email.isEmpty()) {
            binding.tilEmail.error = getString(R.string.missing_fields_error)
            allValid = false
        } else {
            // Vérification du format de l'email
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.tilEmail.error = getString(R.string.invalid_email_format)
                allValid = false
            }
        }

        if (dateOfBirth.isEmpty()) {
            // L'utilisateur n'a pas choisi de date, on affiche une erreur
            binding.tilAddEditDateOfBirth.error = getString(R.string.missing_date_of_birth_error)
            allValid = false
        }

        // Vérification du salaire
        if (salary.isEmpty()) {
            binding.tilSalary.error = getString(R.string.missing_fields_error)
            allValid = false
        }

        // Vérification des notes
        if (notes.isEmpty()) {
            binding.tilNotes.error = getString(R.string.missing_fields_error)
            allValid = false
        }

        return allValid
    }


    /**
     * Affiche l'état de chargement dans l'interface utilisateur.
     */
    private fun showLoading() {
        // Afficher une ProgressBar ou tout autre indicateur visuel
        Toast.makeText(requireContext(), "Loading...", Toast.LENGTH_SHORT).show()
    }

    /**
     * Affiche un message de succès.
     * @param message Le message à afficher.
     */
    private fun showSuccess(message: String, candidateId: Long) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

        val detailFragment = DetailFragment.newInstance(candidateId)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, detailFragment)
            .addToBackStack(null)
            .commit()
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

    companion object {
        private const val ARG_CANDIDATE_ID = "candidate_id"

        /**
         * Crée une instance d'AddEditFragment pour éditer un candidat existant.
         * @param candidateId L'ID du candidat à modifier.
         */
        fun newInstance(candidateId: Long): AddEditFragment {
            val fragment = AddEditFragment()
            val args = Bundle()
            args.putLong(ARG_CANDIDATE_ID, candidateId)
            fragment.arguments = args
            return fragment
        }
    }

}

