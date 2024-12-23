package com.openclassrooms.projet8vitesse.ui.addscreen

import android.app.DatePickerDialog
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import java.util.Calendar

/**
 * Fragment pour ajouter ou modifier un candidat.
 *
 * Ce Fragment affiche simplement l'interface et réagit aux actions de l'utilisateur :
 * - Afficher les champs (prénom, nom, téléphone, email, date de naissance, salaire, notes).
 * - Afficher la photo du candidat (ou un placeholder par défaut).
 * - Permettre de choisir une photo dans la galerie.
 * - Permettre de choisir une date de naissance.
 * - Bouton "Sauvegarder" qui demande au ViewModel d'enregistrer.
 * - Observer les états du ViewModel (succès, erreur, chargement).
 *
 * Le Fragment n'a pas de logique métier. Toute la logique complexe (vérification des champs,
 * insertion en base, etc.) est dans le ViewModel.
 */
@AndroidEntryPoint
class AddEditFragment : Fragment() {

    private var _binding: FragmentAddEditBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddEditViewModel by viewModels()

    // Lanceur pour ouvrir la galerie et récupérer une image
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        // Cette fonction est appelée quand l'utilisateur a sélectionné une image dans la galerie
        if (uri != null) {
            val bitmap = loadBitmapFromUri(uri)
            if (bitmap != null) {
                Log.d("AddEditFragment", "Image chargée avec succès.")
                // On informe le ViewModel de la nouvelle photo
                viewModel.onPhotoSelected(bitmap)
                // On met à jour l'affichage
                binding.candidatePhoto.setImageBitmap(bitmap)
            } else {
                Log.e("AddEditFragment", "Impossible de charger l'image depuis l'URI: $uri")
                Toast.makeText(requireContext(), "Impossible de charger l'image", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.e("AddEditFragment", "URI de l'image est null.")
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


    /**
     * Appelé lorsque la vue est créée.
     * On y fait les initialisations UI et l'observation du ViewModel.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Récupération de l'ID du candidat s'il existe dans les arguments
        val candidateId = arguments?.getLong("candidate_id", -1L) ?: -1L
        // On informe le ViewModel si c'est un ajout ou une édition
        viewModel.init(candidateId)

        setupToolbar()
        setupPhotoClickListener()
        setupDatePickerClickListener()
        setupSaveButtonClickListener()

        observeViewModel()
    }

    /**
     * Configure la barre d'application (toolbar) avec le bouton de retour.
     */
    private fun setupToolbar() {
        binding.topAppBar.setNavigationOnClickListener {
            // On ferme le fragment (revient en arrière)
            parentFragmentManager.popBackStack()
        }
    }

    /**
     * Quand on clique sur la photo, on lance la sélection d'image dans la galerie.
     */
    private fun setupPhotoClickListener() {
        binding.candidatePhoto.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }
    }

    /**
     * Quand on clique sur le champ de date, on ouvre un date picker.
     */
    private fun setupDatePickerClickListener() {
        binding.tieAddEditDateOfBirth.setOnClickListener {
            showDatePicker()
        }
    }

    /**
     * Quand on clique sur le bouton "Sauvegarder", on envoie les données saisies au ViewModel.
     */
    private fun setupSaveButtonClickListener() {
        binding.saveButton.setOnClickListener {
            // On transmet au ViewModel les dernières valeurs saisies par l'utilisateur
            viewModel.onFirstNameChanged(binding.tiFirstname.text.toString())
            viewModel.onLastNameChanged(binding.tiLastname.text.toString())
            viewModel.onPhoneChanged(binding.tiPhone.text.toString())
            viewModel.onEmailChanged(binding.tiEmail.text.toString())
            viewModel.onSalaryChanged(binding.tiSalary.text.toString())
            viewModel.onNotesChanged(binding.tiNotes.text.toString())

            // On demande au ViewModel de sauvegarder
            viewModel.onSaveClicked()
        }
    }

    /**
     * Observe l'état du ViewModel et met à jour l'UI en conséquence.
     */
    private fun observeViewModel() {
        // On utilise repeatOnLifecycle avec Lifecycle.State.STARTED
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    when (state) {
                        is AddEditUiState.Idle -> {

                        }
                        is AddEditUiState.Loading -> {
                            showLoading(true)
                        }
                        is AddEditUiState.Loaded -> {
                            showLoading(false)
                            updateUIWithData(state)
                        }
                        is AddEditUiState.Success -> {
                            showLoading(false)
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                            // Retour à l'écran précédent (liste des candidats)
                            parentFragmentManager.popBackStack()
                        }
                        is AddEditUiState.Error -> {
                            showLoading(false)
                            Toast.makeText(requireContext(), state.error, Toast.LENGTH_SHORT).show()
                        }
                        is AddEditUiState.ErrorMandatoryFields -> {
                            showLoading(false)
                            Toast.makeText(requireContext(), state.error, Toast.LENGTH_SHORT).show()
                            showMandatoryFieldsErrors(state.emptyFields)
                        }
                        is AddEditUiState.ErrorEmailFormat -> {
                            showLoading(false)
                            Toast.makeText(requireContext(), state.error, Toast.LENGTH_SHORT).show()
                            // On peut afficher une erreur sur le champ email
                            binding.tilEmail.error = getString(R.string.mandatory_field_error)
                        }
                    }
                }
            }
        }
    }

    /**
     * Met à jour l'interface avec les données chargées (en mode ajout ou édition).
     */
    private fun updateUIWithData(state: AddEditUiState.Loaded) {
        binding.topAppBar.setTitle(state.titleResId)

        if (state.photo != null) {
            binding.candidatePhoto.setImageBitmap(state.photo)
        } else {
            binding.candidatePhoto.setImageResource(R.drawable.media)
        }

        binding.tiFirstname.setText(state.firstName)
        binding.tiLastname.setText(state.lastName)
        binding.tiPhone.setText(state.phone)
        binding.tiEmail.setText(state.email)
        binding.tiSalary.setText(state.salary)
        binding.tiNotes.setText(state.notes)

        if (state.dateOfBirth != null) {
            binding.tieAddEditDateOfBirth.setText(formatDate(state.dateOfBirth))
        } else {
            binding.tieAddEditDateOfBirth.setText("")
        }

        // Réinitialiser les erreurs (au cas où)
        clearAllErrors()
    }

    /**
     * Affiche ou cache la ProgressBar et les champs.
     * Si isLoading est vrai, on affiche un chargement.
     * Sinon, on affiche le formulaire.
     */
    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.scrollView.visibility = View.GONE
            binding.saveButton.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.scrollView.visibility = View.VISIBLE
            binding.saveButton.visibility = View.VISIBLE
        }
    }

    /**
     * Affiche une boîte de sélection de date (DatePickerDialog).
     * Quand l'utilisateur choisit une date, on met à jour le ViewModel.
     */
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(requireContext(), { _, y, m, d ->
            val chosenDate = ZonedDateTime.of(y, m + 1, d, 0, 0, 0, 0, ZoneId.systemDefault()).toInstant()
            viewModel.onDateOfBirthSelected(chosenDate)
            binding.tieAddEditDateOfBirth.setText(formatDate(chosenDate))
        }, year, month, day)

        // Empêche de sélectionner une date future
        dpd.datePicker.maxDate = System.currentTimeMillis()

        dpd.show()
    }

    /**
     * Convertit un Instant (date-heure universelle) en texte lisible "jj/mm/aaaa".
     */
    private fun formatDate(instant: Instant): String {
        val zdt = instant.atZone(ZoneId.systemDefault())
        val day = zdt.dayOfMonth.toString().padStart(2, '0')
        val month = zdt.monthValue.toString().padStart(2, '0')
        val year = zdt.year.toString()
        return "$day/$month/$year"
    }

    /**
     * Charge un Bitmap à partir d'une Uri (image de la galerie).
     * Retourne le Bitmap ou null si échec.
     */
    private fun loadBitmapFromUri(uri: Uri): android.graphics.Bitmap? {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        return BitmapFactory.decodeStream(inputStream)
    }

    /**
     * Affiche les erreurs sur les champs obligatoires non remplis.
     */
    private fun showMandatoryFieldsErrors(emptyFields: List<AddEditUiState.MandatoryField>) {
        // Réinitialiser toutes les erreurs d'abord
        clearAllErrors()

        // Pour chaque champ vide, afficher une erreur
        emptyFields.forEach { field ->
            when (field) {
                AddEditUiState.MandatoryField.FIRST_NAME -> binding.tilFirstname.error = getString(R.string.mandatory_field_error)
                AddEditUiState.MandatoryField.LAST_NAME -> binding.tilLastname.error = getString(R.string.mandatory_field_error)
                AddEditUiState.MandatoryField.PHONE -> binding.tilPhone.error = getString(R.string.mandatory_field_error)
                AddEditUiState.MandatoryField.EMAIL -> binding.tilEmail.error = getString(R.string.mandatory_field_error)
                AddEditUiState.MandatoryField.DATE_OF_BIRTH -> binding.tilAddEditDateOfBirth.error = getString(R.string.mandatory_field_error)
                AddEditUiState.MandatoryField.EXPECTED_SALARY -> binding.tilSalary.error = getString(R.string.mandatory_field_error)
                AddEditUiState.MandatoryField.NOTES -> binding.tilNotes.error = getString(R.string.mandatory_field_error)
            }
        }
    }

    /**
     * Réinitialise toutes les erreurs.
     */
    private fun clearAllErrors() {
        binding.tilFirstname.error = null
        binding.tilLastname.error = null
        binding.tilPhone.error = null
        binding.tilEmail.error = null
        binding.tilAddEditDateOfBirth.error = null
        binding.tilSalary.error = null
        binding.tilNotes.error = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

