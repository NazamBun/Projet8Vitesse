package com.openclassrooms.projet8vitesse.data.convert

import androidx.room.TypeConverter
import org.threeten.bp.Instant

class InstantConverter {

    @TypeConverter
    fun fromLongNullable(value: Long?): Instant? {
        return value?.let { fromLong(it) }
    }

    @TypeConverter
    fun toLongNullable(instant: Instant?): Long? {
        return instant?.let { toLong(it) }
    }

    @TypeConverter
    fun fromLong(value: Long): Instant {
        return Instant.ofEpochMilli(value)
    }

    @TypeConverter
    fun toLong(instant: Instant): Long {
        return instant.toEpochMilli()
    }

}