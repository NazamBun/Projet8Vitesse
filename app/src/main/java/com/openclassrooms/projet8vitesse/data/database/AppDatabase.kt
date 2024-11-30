package com.openclassrooms.projet8vitesse.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.openclassrooms.projet8vitesse.data.convert.BitmapConverter
import com.openclassrooms.projet8vitesse.data.convert.InstantConverter
import com.openclassrooms.projet8vitesse.data.dao.CandidateDao
import com.openclassrooms.projet8vitesse.data.entity.CandidateDto

// Configuration de la base de données Room
@Database(entities = [CandidateDto::class], version = 1)
@TypeConverters(BitmapConverter::class, InstantConverter::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun candidateDao(): CandidateDao
}