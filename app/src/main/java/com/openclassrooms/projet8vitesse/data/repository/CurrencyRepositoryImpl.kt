package com.openclassrooms.projet8vitesse.data.repository

import android.util.Log
import com.openclassrooms.projet8vitesse.data.remote.CurrencyApiService
import javax.inject.Inject

/**
 * Implémentation de CurrencyRepository utilisant le service Retrofit.
 * Cette classe gère l'appel à l'API pour récupérer les taux de conversion.
 */
class CurrencyRepositoryImpl @Inject constructor(
    private val apiService: CurrencyApiService
) : CurrencyRepository {

    override suspend fun getEurToGbpRate(): Double {
        return try {
            Log.d("CurrencyRepository", "Fetching EUR to GBP rate from API")
            val response = apiService.getRate("eur")
            val rate = response.rates["gbp"] ?: 0.0
            Log.d("CurrencyRepository", "Conversion rate: $rate")
            rate
        } catch (e: Exception) {
            Log.e("CurrencyRepository", "Error fetching conversion rate", e)
            0.0
        }
    }
}

