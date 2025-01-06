package com.openclassrooms.projet8vitesse

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application() {
    // Initialise Instant
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }
}