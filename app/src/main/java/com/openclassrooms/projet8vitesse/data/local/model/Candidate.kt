package com.openclassrooms.projet8vitesse.data.local.model

data class Candidate(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val photoUri: String?,
    val phoneNumber: String,
    val email: String,
    val dateOfBirth: String,
    val expectedSalary: Double,
    val notes: String?,
    val isFavorite: Boolean
)

