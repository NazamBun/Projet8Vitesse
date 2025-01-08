package com.openclassrooms.projet8vitesse
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.openclassrooms.projet8vitesse.data.dao.CandidateDao
import com.openclassrooms.projet8vitesse.data.database.AppDatabase
import com.openclassrooms.projet8vitesse.data.repository.CandidateRepository
import com.openclassrooms.projet8vitesse.data.repository.CandidateRepositoryImpl
import com.openclassrooms.projet8vitesse.domain.model.Candidate

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
class CandidateInstrumentedTest {

    @Inject
    lateinit var candidateDAO: CandidateDao
    private lateinit var candidateRepository: CandidateRepository
    private lateinit var roomDatabase : AppDatabase

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        roomDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        candidateDAO = roomDatabase.candidateDao()
        candidateRepository = CandidateRepositoryImpl(candidateDAO)
    }

    @After
    fun tearDown() {
        roomDatabase.close()
    }

    @Test
    fun insertAndGetAgent() = runTest {
        val candidate = Candidate(id = null, firstName = "John", lastName = "Doe", email = "john@example.com", phoneNumber = "1234567890", note = "fxdgsd")

        val id = candidateRepository.insertCandidate(candidate)
        assertTrue(id > 0)

        candidateRepository.getById(id).collect{
            candidateFromDb ->
            assertNotNull(candidateFromDb)
            assertEquals(candidate.firstName, candidateFromDb?.firstName)
            assertEquals(candidate.lastName, candidateFromDb?.lastName)
        }


    }


}