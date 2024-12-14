package com.openclassrooms.projet8vitesse.data.repository

import com.openclassrooms.projet8vitesse.data.remote.CurrencyApiService
import javax.inject.Inject

/**
 * Implémentation de CurrencyRepository utilisant le service Retrofit.
 */
class CurrencyRepositoryImpl @Inject constructor(
    private val apiService: CurrencyApiService
) : CurrencyRepository {

    override suspend fun getEurToGbpRate(): Double {
        // Appel générique avec les paramètres "eur" et "gbp"
        val response = apiService.getRate("eur", "gbp")
        return response.gbp ?: 0.0 // utilisez une valeur par défaut si null
    }
}
