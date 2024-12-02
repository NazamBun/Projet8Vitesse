package com.openclassrooms.projet8vitesse.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.openclassrooms.projet8vitesse.R
import com.openclassrooms.projet8vitesse.databinding.ActivityMainBinding
import com.openclassrooms.projet8vitesse.ui.addscreen.AddEditFragment
import com.openclassrooms.projet8vitesse.ui.homescrreen.HomeFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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
            navigateToHome()
        }
    }

    /**
     * Remplace le conteneur principal par HomeFragment.
     */
    private fun navigateToHome() {
        supportFragmentManager.commit {
            replace(R.id.main, HomeFragment())
            addToBackStack(null)
        }
    }

    /**
     * Remplace le conteneur principal par AddEditFragment.
     */
    fun navigateToAddEdit() {
        supportFragmentManager.commit {
            replace(R.id.main, AddEditFragment())
            addToBackStack(null)
        }
    }
}
