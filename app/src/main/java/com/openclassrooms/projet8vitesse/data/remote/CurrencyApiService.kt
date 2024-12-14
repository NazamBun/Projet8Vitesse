package com.openclassrooms.projet8vitesse.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Interface Retrofit pour appeler l'API de conversion des devises.
 * L'API permet de récupérer des taux de conversion au format JSON.
 *
 * Exemple d'endpoint : eur/gbp.json
 * L'URL finale est construite ainsi :
 * BASE_URL + "{from}/{to}.json"
 * où {from} et {to} sont les codes ISO des devises (ex: "eur", "gbp").
 */
interface CurrencyApiService {

    /**
     * Récupère le taux de conversion entre deux devises.
     *
     * @param from Devise source (ex: "eur").
     * @param to Devise cible (ex: "gbp").
     * @return Un CurrencyResponse avec la date et le taux de conversion.
     *
     * Exemple de requête : GET https://.../eur/gbp.json
     */
    @GET("{from}/{to}.json")
    suspend fun getRate(
        @Path("from") from: String,
        @Path("to") to: String
    ): CurrencyResponse
}
