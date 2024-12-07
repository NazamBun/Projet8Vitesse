package com.openclassrooms.projet8vitesse.utils

import android.graphics.Bitmap
import android.graphics.Canvas

object BitmapUtils {
    fun create(width: Int, height: Int): Bitmap {
        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
            val canvas = Canvas(this)
            canvas.drawColor(android.graphics.Color.WHITE)
        }
    }
}