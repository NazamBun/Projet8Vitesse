package com.openclassrooms.projet8vitesse.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.openclassrooms.projet8vitesse.data.entity.CandidateDto
import kotlinx.coroutines.flow.Flow

// Interface DAO pour gérer les opérations sur les candidats
@Dao
interface CandidateDao {

    // Lire tous les candidats
    @Query("SELECT * FROM candidates ORDER BY last_name, first_name")
    fun getAllCandidates(): Flow<List<CandidateDto>>

    // Lire les candidats favoris
    @Query("""
        SELECT * 
        FROM candidates 
        WHERE (:favorite IS NULL OR is_favorite = :favorite) AND 
        (:name IS NULL OR (first_name LIKE '%' || :name|| '%' OR last_name LIKE '%' || :name || '%'))
        """)
    fun getCandidates(favorite: Boolean?, name: String?): Flow<List<CandidateDto>>

    // Lire un candidat par son ID
    @Query("SELECT * FROM candidates WHERE id = :id ")
    fun getById(id: Long): Flow<CandidateDto?>

    // Insérer un nouveau candidat
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCandidate(candidate: CandidateDto): Long

    // Mettre à jour un candidat
    @Update
    suspend fun updateCandidate(candidate: CandidateDto): Int

    // Supprimer un candidat
    @Delete
    suspend fun deleteCandidate(candidate: CandidateDto)

    // Supprimer tous les candidats
    @Query("DELETE FROM candidates")
    suspend fun deleteAllCandidates()



    // Mettre à jour le statut de favori d'un candidat
    @Query("UPDATE candidates SET is_favorite = :favorite WHERE id = :id")
    suspend fun updateCandidate(id: Long, favorite: Boolean) : Int

}