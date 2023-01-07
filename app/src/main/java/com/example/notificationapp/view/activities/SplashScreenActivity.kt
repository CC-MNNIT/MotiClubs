package com.example.notificationapp.view.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notificationapp.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()

        val text = findViewById<TextView>(R.id.text)
        val image = findViewById<ShapeableImageView>(androidx.appcompat.R.id.image)

        image.animate().translationY(-1600f).setDuration(1000).startDelay = 3000
        text.animate().translationY(1400f).setDuration(1000).startDelay = 3000

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