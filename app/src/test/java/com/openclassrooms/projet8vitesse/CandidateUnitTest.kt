package com.openclassrooms.projet8vitesse
import android.graphics.Bitmap
import android.graphics.Canvas
import com.openclassrooms.projet8vitesse.data.dao.CandidateDao
import com.openclassrooms.projet8vitesse.data.entity.CandidateDto
import com.openclassrooms.projet8vitesse.data.repository.CandidateRepository
import com.openclassrooms.projet8vitesse.data.repository.CandidateRepositoryImpl
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.threeten.bp.Instant
import java.time.LocalDateTime

class CandidateUnitTest {
    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var candidateRepository: CandidateRepository
    private lateinit var candidateDao: CandidateDao

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        candidateRepository = CandidateRepositoryImpl(candidateDao)
    }

    @Test
    fun `test getAllCandidates should return list of candidates`() = runTest {
        val candidateList = listOf(
            CandidateDto(1,
                "doe",
                "john",
                create(100,100),
                "021321326544",
                "a@agmail.com",
                Instant.now(),
                12365,
                "kjlhklhklhkl",
                true
                ),
            //CandidateDto()
            )
        Mockito.`when`(candidateDao.getAllCandidates()).thenReturn(flowOf(candidateList))
        val res = candidateRepository.getAllCandidates()

        //assertEquals(1, res.collect())
        //assertEquals("doe", res[0].firstName)
    }

    fun create(width: Int, height: Int): Bitmap {
        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
            val canvas = Canvas(this)
            canvas.drawColor(android.graphics.Color.WHITE)
        }
    }



}