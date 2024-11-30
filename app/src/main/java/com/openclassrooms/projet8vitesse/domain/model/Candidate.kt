package com.openclassrooms.projet8vitesse.domain.model

import android.graphics.Bitmap
import com.openclassrooms.projet8vitesse.data.entity.CandidateDto
import java.time.Instant

data class Candidate(
    val id: Long? = null,
    val firstName: String,
    val lastName: String,
    val photo: Bitmap,
    val phoneNumber: String,
    val email: String,
    val dateOfBirth: Instant,
    val expectedSalary: Int,
    val note: String?,
    val isFavorite: Boolean
) {
    fun toDto(): CandidateDto {
        return CandidateDto(
            id = this.id?:0,
            firstName = this.firstName,
            lastName = this.lastName,
            photo = this.photo,
            phoneNumber = this.phoneNumber,
            email = this.email,
            dateOfBirth = this.dateOfBirth,
            expectedSalary = this.expectedSalary,
            note = this.note,
            isFavorite = this.isFavorite
        )
    }

    companion object {
        fun fromDTO(candiateDto: CandidateDto): Candidate {
            return candiateDto.toModel()
        }
    }
}

