package com.openclassrooms.projet8vitesse.ui.detailscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.openclassrooms.projet8vitesse.databinding.FragmentDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: Ajouter l'action pour supprimer un candidat après création du fichier XML
        /*
        binding.deleteButton.setOnClickListener {
            val candidate = // Récupérez le candidat affiché
                viewModel.deleteCandidate(candidate)
        }
        */

        // TODO: Ajouter l'action pour mettre à jour le statut favori après création du fichier XML
        /*
        binding.favoriteButton.setOnClickListener {
            val candidate = // Récupérez le candidat affiché
                viewModel.toggleFavorite(candidate)
        }
        */
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
