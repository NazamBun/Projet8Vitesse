package com.openclassrooms.projet8vitesse.domain.model

import android.graphics.Bitmap
import com.openclassrooms.projet8vitesse.data.entity.CandidateDto
import org.threeten.bp.Instant


data class Candidate(
    val id: Long? = null,
    val firstName: String = "",
    val lastName: String = "",
    val photo: Bitmap? = null, // Autorise null pour éviter le problème de compatibilité
    val phoneNumber: String = "",
    val email: String = "",
    val dateOfBirth: Instant = Instant.EPOCH,
    val expectedSalary: Int = 0,
    val note: String? = null,
    val isFavorite: Boolean = false
) {
    fun toDto(): CandidateDto {
        return CandidateDto(
            id = this.id?:0,
            firstName = this.firstName,
            lastName = this.lastName,
            photo = this.photo?: defaultPlaceholderBitmap(),
            phoneNumber = this.phoneNumber,
            email = this.email,
            dateOfBirth = this.dateOfBirth,
            expectedSalary = this.expectedSalary,
            note = this.note,
            isFavorite = this.isFavorite
        )
    }

    companion object {
        fun fromDTO(candidateDto: CandidateDto): Candidate {
            return candidateDto.toModel()
        }
    }

    private fun defaultPlaceholderBitmap(): Bitmap {
        // Crée un Bitmap par défaut ou charge une ressource
        return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    }
}

