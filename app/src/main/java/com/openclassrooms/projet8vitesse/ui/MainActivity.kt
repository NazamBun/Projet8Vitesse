package com.openclassrooms.projet8vitesse.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.openclassrooms.projet8vitesse.R
import com.openclassrooms.projet8vitesse.databinding.ActivityMainBinding
import com.openclassrooms.projet8vitesse.ui.homescrreen.HomeFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialiser ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Charger HomeFragment au démarrage
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.main, HomeFragment())
            }
        }
    }
}
