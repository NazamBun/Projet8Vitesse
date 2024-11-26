package com.openclassrooms.projet8vitesse.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

// Représente un candidat dans la base de données
@Entity(tableName = "candidates")
data class CandidateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val firstName: String,
    val lastName: String,
    val photoUrl: String?,
    val phoneNumber: String,
    val email: String,
    val dateOfBirth: String,
    val expectedSalary: Double,
    val notes: String?,
    val isFavorite: Boolean = false
)