package com.example.xperience_appfinal

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.xperience_appfinal.databinding.ActivityAuthBinding
import com.example.xperience_appfinal.R

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            showLoginFragment()
        }
        setupTabListeners()
    }

    private fun setupTabListeners() {
        binding.tabLogin.setOnClickListener { showLoginFragment() }
        binding.tabRegister.setOnClickListener { showRegisterFragment() }
    }

    private fun showLoginFragment() {
        replaceFragment(LoginFragment())
        updateTabStyles(isLoginSelected = true)
    }

    private fun showRegisterFragment() {
        replaceFragment(RegisterFragment())
        updateTabStyles(isLoginSelected = false)
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun updateTabStyles(isLoginSelected: Boolean) {
        if (isLoginSelected) {
            binding.tabLogin.setBackgroundResource(R.drawable.tab_selected_bg)
            binding.tabLogin.setTextColor(Color.WHITE)
            binding.tabRegister.setBackgroundResource(android.R.color.transparent)
            binding.tabRegister.setTextColor(ContextCompat.getColor(this, R.color.text_inactive))
        } else {
            binding.tabLogin.setBackgroundResource(android.R.color.transparent)
            binding.tabLogin.setTextColor(ContextCompat.getColor(this, R.color.text_inactive))
            binding.tabRegister.setBackgroundResource(R.drawable.tab_selected_bg)
            binding.tabRegister.setTextColor(Color.WHITE)
        }
    }
}