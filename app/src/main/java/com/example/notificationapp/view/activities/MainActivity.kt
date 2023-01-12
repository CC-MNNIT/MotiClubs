package com.example.notificationapp.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.notificationapp.R
import com.example.notificationapp.databinding.ActivityMainBinding
import com.example.notificationapp.view.fragments.HelpFragment
import com.example.notificationapp.view.fragments.HomeFragment
import com.example.notificationapp.view.fragments.ProfileFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment(this)).commit()
        setListeners()
    }

    private fun setListeners() {
        binding.navView.setOnItemSelectedListener {
            supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container, when (it.itemId) {
                    R.id.profile_section -> ProfileFragment()
                    R.id.help -> HelpFragment()
                    else -> HomeFragment(this)
                }
            ).commit()
            true
        }
    }

    fun goToProfilePage() {
        binding.navView.selectedItemId = R.id.profile_section
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ProfileFragment()).commit()
    }
}