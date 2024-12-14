package com.openclassrooms.projet8vitesse.data.remote

import com.google.gson.annotations.SerializedName

/**
 * Classe de données pour représenter la réponse JSON de l'API de conversion de devises.
 * Format attendu :
 * {
 *   "date": "yyyy-MM-dd",
 *   "gbp": valeur (ex: 0.86)
 * }
 *
 * Remarque : Le nom du champ dépend de la devise "to". Si c'est "gbp", on s'attend à un champ "gbp".
 * Dans ce cas, pour simplifier, supposons que vous savez toujours à l'avance quelle devise est ciblée.
 * Sinon, il faudrait un parsing plus dynamique.
 */
data class CurrencyResponse(
    val date: String,
    @SerializedName("gbp") val gbp: Double? // Si vous utilisez toujours gbp en devise cible.
)
