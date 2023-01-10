package com.example.notificationapp.view.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.notificationapp.app.UserInstance
import com.example.notificationapp.databinding.ActivitySplashScreenBinding
import com.google.firebase.auth.FirebaseAuth

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: ActivitySplashScreenBinding

    private val requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
            openApp()
        } else {
            Toast.makeText(
                this, "Please enable notification permission for this app",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition { true }
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()

        binding.image.animate().translationY(-1600f).setDuration(1000).startDelay = 3000
        binding.text.animate().translationY(1400f).setDuration(1000).startDelay = 3000

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            validateNotificationPermission()
        } else {
            openApp()
        }
    }

    private fun openApp() {
        val user = mAuth.currentUser
        if (user != null) {
            if (user.isEmailVerified) {
                UserInstance.refreshUserSession(user, applicationContext, {
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()
                }) {
                    mAuth.signOut()
                    Toast.makeText(this, "Error: Refresh login session", Toast.LENGTH_LONG).show()
                    startActivity(Intent(applicationContext, LoginActivity::class.java))
                    finish()
                }
            } else {
                mAuth.signOut()
                Toast.makeText(applicationContext, "Please Verify Your Email.", Toast.LENGTH_LONG).show()
                startActivity(Intent(applicationContext, LoginActivity::class.java))
                finish()
            }
        } else {
            startActivity(Intent(applicationContext, IntroSliderActivity::class.java))
            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun validateNotificationPermission() {
        when {
            (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS))
                    == PackageManager.PERMISSION_GRANTED -> {
                openApp()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                Toast.makeText(
                    this, "Please enable notification permission for this app",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
                return
            }
            else -> {
                requestPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}