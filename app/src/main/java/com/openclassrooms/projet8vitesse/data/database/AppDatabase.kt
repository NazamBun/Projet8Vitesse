package com.openclassrooms.projet8vitesse.data.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.openclassrooms.projet8vitesse.data.convert.BitmapConverter
import com.openclassrooms.projet8vitesse.data.convert.InstantConverter
import com.openclassrooms.projet8vitesse.data.dao.CandidateDao
import com.openclassrooms.projet8vitesse.data.entity.CandidateDto
import com.openclassrooms.projet8vitesse.utils.BitmapUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.threeten.bp.Instant


// Configuration de la base de données Room
@Database(entities = [CandidateDto::class], version = 1)
@TypeConverters(BitmapConverter::class, InstantConverter::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun candidateDao(): CandidateDao


    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            Log.d(TAG, "onCreate called")
            INSTANCE?.let { database ->
                scope.launch {
                    initDatabase(
                        database.candidateDao()

                    )
                }
            }
        }

        suspend fun initDatabase(
            candidateDao: CandidateDao
        ) {

            Log.d(TAG, "initializing Database")

            // candidate Insert
            val idCandidate1 = candidateDao.insertCandidate(
                CandidateDto(
                    id = 1,
                    firstName = "John",
                    lastName = "Doe",
                    photo = BitmapUtils.create(400,400),
                    phoneNumber = "123456789",
                    email = "john.doe@email.com",
                    dateOfBirth = Instant.now(),
                    expectedSalary = 1000,
                    note = "ma note",
                    isFavorite = false
                )
            )

            Log.d(TAG, "Candidate 1 inserted with id $idCandidate1")


        }
    }




    companion object {
        private const val TAG = "AppDatabase"
        private const val DATABASE_NAME = "CandidateDB"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, coroutineScope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .addCallback(AppDatabaseCallback(coroutineScope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}