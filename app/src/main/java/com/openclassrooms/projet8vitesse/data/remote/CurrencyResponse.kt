package com.openclassrooms.projet8vitesse.data.remote

import com.squareup.moshi.Json

/**
 * Classe de données représentant la réponse de l'API de conversion des devises.
 */
data class CurrencyResponse(
    @Json(name = "date") val date: String,                // Date de la réponse
    @Json(name = "eur") val rates: Map<String, Double>    // Taux imbriqués sous "eur"
)
