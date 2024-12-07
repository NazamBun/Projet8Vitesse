package com.openclassrooms.projet8vitesse.utils

import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

object DateUtils {
    val sDateFormatter =
        DateTimeFormatter.ofPattern("dd/MM/yy")

    val sLocaleZone = ZoneId.of("Europe/Paris")

    fun computeInstantFromLocalDate(year: Int, month: Int, day: Int): Instant {
        return ZonedDateTime
            .now(sLocaleZone)
            .withYear(year)
            // instant API start at month 1, not 0
            .withMonth(month + 1)
            .withDayOfMonth(day)
            .toInstant()
    }

    fun localeDateTimeStringFromInstant(instant: Instant?): String? {
        if (instant == null) return null
        val zdt = instant.atZone(sLocaleZone)
        return zdt.format(sDateFormatter)
    }
}