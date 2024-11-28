package com.openclassrooms.projet8vitesse.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "candidates")
data class Candidate(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val firstName: String,
    val lastName: String,
    val photoUri: String?,
    val note: String?,
    val isFavorite: Boolean
)
