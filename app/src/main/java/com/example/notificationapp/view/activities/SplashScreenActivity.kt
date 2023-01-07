package com.example.notificationapp.view.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notificationapp.R
import com.example.notificationapp.databinding.ActivitySplashScreenBinding
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()

        binding.image.animate().translationY(-1600f).setDuration(1000).startDelay = 3000
        binding.text.animate().translationY(1400f).setDuration(1000).startDelay = 3000

        val handler = Handler(mainLooper)
        handler.postDelayed({
            if (mAuth.currentUser != null) {
                if (mAuth.currentUser!!.isEmailVerified) {
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    mAuth.signOut()
                    Toast.makeText(applicationContext, "Please Verify Your Email.", Toast.LENGTH_LONG).show()
                }
            } else {
                val intent = Intent(applicationContext, IntroSliderActivity::class.java)
                startActivity(intent)
            }
            finish()
        }, 4000)
    }
}