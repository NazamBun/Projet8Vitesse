package com.openclassrooms.projet8vitesse.ui.addscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.openclassrooms.projet8vitesse.R
import com.openclassrooms.projet8vitesse.databinding.FragmentAddEditBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddEditFragment : Fragment() {

    private var _binding: FragmentAddEditBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddEditViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        // Définir le titre en fonction du mode (Ajout ou Édition)
        val isEditMode = arguments?.getBoolean("isEditMode", false) ?: false
        binding.topAppBar.title = if (isEditMode) {
            getString(R.string.edit_candidate)
        } else {
            getString(R.string.add_candidate)
        }

        // Gestion de l'icône de retour
        binding.topAppBar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }


        binding.saveButton.setOnClickListener {
            saveCandidate()
        }
    }

    /**
     * Récupère les valeurs des champs de saisie, les valide,
     * et sauvegarde un nouveau candidat ou met à jour l'existant
     */
    private fun saveCandidate() {
        // Récupérer les valeurs des champs
        val firstName = binding.tiFirstname.text.toString().trim()
        val lastName = binding.tiLastname.text.toString().trim()
        val email = binding.tiEmail.text.toString().trim()
        val phoneNumber = binding.tiPhone.text.toString().trim()
        val notes = binding.tiNotes.text.toString().trim()
        val expectedSalaryString = binding.tiSalary.text.toString().trim()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
