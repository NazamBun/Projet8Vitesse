package com.openclassrooms.projet8vitesse.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.openclassrooms.projet8vitesse.data.local.entities.CandidateEntity
import kotlinx.coroutines.flow.Flow

// Interface DAO pour gérer les opérations sur les candidats
@Dao
interface CandidateDao {

    // Insérer un nouveau candidat
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCandidate(candidate: CandidateEntity)

    // Lire tous les candidats
    @Query("SELECT * FROM candidates")
    fun getAllCandidates(): Flow<List<CandidateEntity>>

    // Lire les candidats favoris
    @Query("SELECT * FROM candidates WHERE isFavorite = 1")
    fun getFavoriteCandidates(): Flow<List<CandidateEntity>>

    // Mettre à jour un candidat
    @Update
    suspend fun updateCandidate(candidate: CandidateEntity)

    // Supprimer un candidat
    @Delete
    suspend fun deleteCandidate(candidate: CandidateEntity)

    // Supprimer tous les candidats
    @Query("DELETE FROM candidates")
    suspend fun deleteAllCandidates()

}