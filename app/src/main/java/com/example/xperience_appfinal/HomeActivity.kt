package com.example.xperience_appfinal

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.xperience_appfinal.databinding.ActivityHomeBinding


class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    private val navItems by lazy {
        mapOf(
            R.id.nav_home to Triple(binding.iconHome, binding.textHome, HomeFragment()),
            R.id.nav_explore to Triple(binding.iconExplore, binding.textExplore, ExploreFragment()),
            R.id.nav_missions to Triple(binding.iconMissions, binding.textMissions, MissionsFragment()),
            R.id.nav_profile to Triple(binding.iconProfile, binding.textProfile, ProfileFragment())
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupNavListeners()
        if (savedInstanceState == null) {
            selectFragment(R.id.nav_home)
        }
    }

    private fun setupNavListeners() {
        binding.navHome.setOnClickListener { selectFragment(R.id.nav_home) }
        binding.navExplore.setOnClickListener { selectFragment(R.id.nav_explore) }
        binding.navMissions.setOnClickListener { selectFragment(R.id.nav_missions) }
        binding.navProfile.setOnClickListener { selectFragment(R.id.nav_profile) }
    }

    private fun selectFragment(selectedId: Int) {
        val fragmentToShow = navItems[selectedId]?.third ?: return
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment_container, fragmentToShow)
            .commit()
        updateNavStyles(selectedId)
    }

    private fun updateNavStyles(selectedId: Int) {
        val activeColor = ContextCompat.getColor(this, R.color.nav_active)
        val inactiveColor = ContextCompat.getColor(this, R.color.nav_inactive)

        navItems.forEach { (id, items) ->
            val (icon, text, _) = items
            if (id == selectedId) {
                icon.setColorFilter(activeColor)
                text.setTextColor(activeColor)
            } else {
                icon.setColorFilter(inactiveColor)
                text.setTextColor(inactiveColor)
            }
        }
    }

}