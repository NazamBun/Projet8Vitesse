package com.openclassrooms.projet8vitesse.utils

import com.openclassrooms.projet8vitesse.data.local.entities.CandidateEntity
import com.openclassrooms.projet8vitesse.data.local.model.Candidate

fun CandidateEntity.toCandidate(): Candidate {
    return Candidate(
        id = this.id,
        firstName = this.firstName,
        lastName = this.lastName,
        photoUri = this.photoUri,
        phoneNumber = this.phoneNumber,
        email = this.email,
        dateOfBirth = this.dateOfBirth,
        expectedSalary = this.expectedSalary,
        notes = this.notes,
        isFavorite = this.isFavorite
    )
}

/**
 * Mapper pour convertir une liste de CandidateEntity en une liste de Candidate.
 */
fun List<CandidateEntity>.toCandidateList(): List<Candidate> {
    return this.map { it.toCandidate() }
}