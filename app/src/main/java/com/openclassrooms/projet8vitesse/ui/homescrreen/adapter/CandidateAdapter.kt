package com.openclassrooms.projet8vitesse.presentation.ui.homescreen.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.projet8vitesse.databinding.ItemCandidateBinding
import com.openclassrooms.projet8vitesse.domain.model.Candidate

/**
 * Adapter pour afficher la liste des candidats dans un RecyclerView.
 *
 * @param onItemClick Fonction déclenchée lors du clic sur un candidat.
 */
class CandidateAdapter(
    private val onItemClick: (Candidate) -> Unit
) : ListAdapter<Candidate, CandidateAdapter.CandidateViewHolder>(CandidateDiffCallback()) {

    /**
     * ViewHolder pour un élément candidat.
     *
     * @param binding Liaison des vues avec le layout XML.
     */
    class CandidateViewHolder(
        private val binding: ItemCandidateBinding,
        private val onItemClick: (Candidate) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        /**
         * Lie un candidat aux vues.
         *
         * @param candidate Le candidat à afficher.
         */
        fun bind(candidate: Candidate) {
            binding.candidateName.text = "${candidate.firstName} ${candidate.lastName}"
            binding.candidateNote.text = candidate.note ?: ""
            binding.candidatePhoto.setImageBitmap(candidate.photo)

            // Clic sur l'élément
            binding.root.setOnClickListener {
                onItemClick(candidate)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CandidateViewHolder {
        val binding = ItemCandidateBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CandidateViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: CandidateViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

/**
 * Callback pour déterminer les différences entre deux candidats.
 */
class CandidateDiffCallback : DiffUtil.ItemCallback<Candidate>() {
    override fun areItemsTheSame(oldItem: Candidate, newItem: Candidate): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Candidate, newItem: Candidate): Boolean {
        return oldItem == newItem
    }
}
