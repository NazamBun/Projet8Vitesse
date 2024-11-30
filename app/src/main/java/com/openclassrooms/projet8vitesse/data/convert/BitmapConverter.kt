package com.openclassrooms.projet8vitesse.data.convert

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

class BitmapConverter {

    companion object {
        private const val COMPRESSION_QUALITY = 100
        private const val START_OFFSET = 0
        private val FORMAT = Bitmap.CompressFormat.PNG
    }

    @TypeConverter
    fun toBitmap(bytes: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(bytes, START_OFFSET, bytes.size)
    }

    @TypeConverter
    fun fromBitmap(img: Bitmap): ByteArray {
        val os = ByteArrayOutputStream()
        img.compress(FORMAT, COMPRESSION_QUALITY, os)
        return os.toByteArray()
    }
}