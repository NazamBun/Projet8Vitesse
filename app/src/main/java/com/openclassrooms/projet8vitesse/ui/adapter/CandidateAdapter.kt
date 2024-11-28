package com.openclassrooms.projet8vitesse.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.layout.Layout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.projet8vitesse.R
import com.openclassrooms.projet8vitesse.data.local.model.Candidate
import com.openclassrooms.projet8vitesse.databinding.ItemCandidateBinding


/**
 * Adapter pour le RecyclerView affichant la liste des candidats.
 *
 * @param onItemClicked Callback exécuté lorsqu'un élément est cliqué.
 */
class CandidateAdapter(
    private val onItemClicked: (Candidate) -> Unit
): ListAdapter<Candidate, CandidateAdapter.CandidateViewHolder>(DiffCallback) {

    /**
     * ViewHolder pour afficher un candidat.
     *
     * @param binding Liaison avec le layout XML pour un candidat.
     */
    class CandidateViewHolder(private val binding: ItemCandidateBinding) : RecyclerView.ViewHolder(binding.root) {

        /**
         * Lie les données du candidat à la vue.
         *
         * @param candidate Données du candidat à afficher.
         * @param onItemClicked Callback à exécuter lorsque l'élément est cliqué.
         */
        fun bind(candidate: Candidate, onItemClicked: (Candidate) -> Unit) {
            // Met à jour les vues avec les données du candidat
            binding.candidateName.text = "${candidate.firstName} ${candidate.lastName}"
            binding.candidateNote.text = candidate.note
            binding.candidatePhoto.setImageResource(R.drawable.ic_person) // Placeholder pour la photo

            // Définit un clic sur la vue pour exécuter le callback
            binding.root.setOnClickListener { onItemClicked(candidate) }
        }
    }

    companion object {
        /**
         * DiffUtil pour comparer les éléments et optimiser les performances.
         */
        private val DiffCallback = object :  DiffUtil.ItemCallback<Candidate>() {
            /**
             * Vérifie si deux éléments représentent le même candidat (par leur ID).
             *
             * @param oldItem Ancien élément.
             * @param newItem Nouvel élément.
             * @return `true` si les deux éléments ont le même ID, sinon `false`.
             */
            override fun areItemsTheSame(oldItem: Candidate, newItem: Candidate): Boolean {
                return oldItem.id == newItem.id
            }
            /**
             * Vérifie si le contenu de deux éléments est identique.
             *
             * @param oldItem Ancien élément.
             * @param newItem Nouvel élément.
             * @return `true` si le contenu est identique, sinon `false`.
             */
            override fun areContentsTheSame(oldItem: Candidate, newItem: Candidate): Boolean {
                return oldItem == newItem
            }
        }
    }

    /**
     * Crée un ViewHolder pour un élément de la liste.
     *
     * @param parent Vue parente où le ViewHolder sera attaché.
     * @param viewType Type de vue (utilisé si plusieurs types sont gérés).
     * @return Un nouveau ViewHolder pour un élément.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CandidateViewHolder {
        val binding = ItemCandidateBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CandidateViewHolder(binding)
    }

    /**
     * Lie les données d'un candidat à un ViewHolder.
     *
     * @param holder ViewHolder où les données seront affichées.
     * @param position Position de l'élément dans la liste.
     */
    override fun onBindViewHolder(holder: CandidateViewHolder, position: Int) {
        val candidate = getItem(position)
        holder.bind(candidate, onItemClicked)
    }
}