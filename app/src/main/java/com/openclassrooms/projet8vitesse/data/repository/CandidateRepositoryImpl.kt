package com.openclassrooms.projet8vitesse.data.repository

import com.openclassrooms.projet8vitesse.data.dao.CandidateDao
import com.openclassrooms.projet8vitesse.data.entity.CandidateDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CandidateRepositoryImpl @Inject constructor(
    private val candidateDao: CandidateDao // DAO injecté via Hilt
) : CandidateRepository {
    override fun getAllCandidates(): Flow<List<CandidateDto>> {
        return candidateDao.getAllCandidates()
    }

    override fun getFavoriteCandidates(): Flow<List<CandidateDto>> {
        return candidateDao.getFavoriteCandidates()
    }

    override suspend fun insertCandidate(candidate: CandidateDto) {
        candidateDao.insertCandidate(candidate)
    }

    override suspend fun deleteCandidate(candidate: CandidateDto) {
        candidateDao.insertCandidate(candidate)
    }

    override suspend fun deleteAllCandidates() {
        candidateDao.deleteAllCandidates()
    }

}