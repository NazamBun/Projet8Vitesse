package com.openclassrooms.projet8vitesse.data.remote

import com.squareup.moshi.Json

/**
 * Classe représentant la réponse JSON pour les taux de conversion.
 *
 * Exemple de réponse :
 * {
 *   "date": "2024-06-17",
 *   "rates": {
 *     "usd": 1.07,
 *     "gbp": 0.85,
 *     ...
 *   }
 * }
 */
data class CurrencyResponse(
    @Json(name = "date") val date: String,                // Date de la réponse
    @Json(name = "eur") val rates: Map<String, Double>    // Taux imbriqués sous "eur"
)
