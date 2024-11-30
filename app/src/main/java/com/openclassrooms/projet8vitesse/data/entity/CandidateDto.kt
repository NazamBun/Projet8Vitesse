package com.openclassrooms.projet8vitesse.data.entity

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.openclassrooms.projet8vitesse.domain.model.Candidate
import java.time.Instant

// Représente un candidat dans la base de données
@Entity(tableName = "candidates")
data class CandidateDto(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "first_name")
    val firstName: String,

    @ColumnInfo(name = "last_name")
    val lastName: String,

    @ColumnInfo(name = "photo")
    val photo: Bitmap,

    @ColumnInfo(name = "phone_number")
    val phoneNumber: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "date_of_birth")
    val dateOfBirth: Instant,

    @ColumnInfo(name = "expected_salary")
    val expectedSalary: Int,

    @ColumnInfo(name = "note")
    val note: String?,

    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false
) {
    fun toModel(): Candidate {
        return Candidate(
            id=if (this.id == 0L) null else this.id,
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
}