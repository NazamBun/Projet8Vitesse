package com.openclassrooms.projet8vitesse.data.repository

/**
 * Interface pour récupérer le taux de conversion EUR -> GBP.
 *
 * Cette interface fait partie de la couche Domain/Data.
 * Le ViewModel passera par un Use Case, qui appelle ce repository.
 */
interface CurrencyRepository {
    /**
     * Récupère le taux EUR -> GBP.
     * @return Le taux (double).
     */
    suspend fun getEurToGbpRate(): Double
}
