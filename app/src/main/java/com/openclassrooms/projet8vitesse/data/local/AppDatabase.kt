package com.openclassrooms.projet8vitesse.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.openclassrooms.projet8vitesse.data.local.dao.CandidateDao
import com.openclassrooms.projet8vitesse.data.local.entities.CandidateEntity

// Configuration de la base de données Room
@Database(entities = [CandidateEntity::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun candidateDao(): CandidateDao
}