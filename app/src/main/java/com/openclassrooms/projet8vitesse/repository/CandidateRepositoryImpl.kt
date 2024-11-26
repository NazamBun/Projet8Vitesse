package com.openclassrooms.projet8vitesse.repository

import com.openclassrooms.projet8vitesse.data.local.dao.CandidateDao
import com.openclassrooms.projet8vitesse.data.local.entities.CandidateEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CandidateRepositoryImpl @Inject constructor(
    private val candidateDao: CandidateDao // DAO injecté via Hilt
) : CandidateRepository {
    override fun getAllCandidates(): Flow<List<CandidateEntity>> {
        return candidateDao.getAllCandidates()
    }

    override fun getFavoriteCandidates(): Flow<List<CandidateEntity>> {
        return candidateDao.getFavoriteCandidates()
    }

    override suspend fun insertCandidate(candidate: CandidateEntity) {
        candidateDao.insertCandidate(candidate)
    }

    override suspend fun deleteCandidate(candidate: CandidateEntity) {
        candidateDao.insertCandidate(candidate)
    }

    override suspend fun deleteAllCandidates() {
        candidateDao.deleteAllCandidates()
    }

}