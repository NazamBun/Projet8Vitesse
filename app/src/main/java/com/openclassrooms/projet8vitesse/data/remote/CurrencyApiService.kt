package com.openclassrooms.projet8vitesse.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Interface Retrofit pour appeler l'API de conversion des devises.
 * Utilise le nouveau format de l'API : /currencies/{currencyCode}.json
 */
interface CurrencyApiService {

    /**
     * Récupère les taux de conversion pour une devise donnée.
     *
     * @param from Devise source (ex: "eur").
     * @return Une réponse contenant les taux de conversion.
     *
     * Exemple : GET /currencies/eur.json
     */
    @GET("currencies/{from}.json")
    suspend fun getRate(
        @Path("from") from: String
    ): CurrencyResponse
}
