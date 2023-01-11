package com.example.notificationapp.view.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.notificationapp.R
import com.example.notificationapp.adapters.IntroSliderAdapter
import com.example.notificationapp.api.IntroSlide
import com.example.notificationapp.databinding.ActivityIntroSliderBinding

class IntroSliderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIntroSliderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroSliderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val slideList = listOf(
            IntroSlide(
                "welcome",
                "welcome",
                R.drawable.campus
            ),
            IntroSlide(
                "welcome",
                "welcome",
                R.drawable.campus
            ),
            IntroSlide(
                "welcome",
                "welcome",
                R.drawable.campus
            )
        )

        // creating adapter object
        val sliderAdapter = IntroSliderAdapter(slideList)

        // setting adapter to the view
        binding.introSliderViewPager.adapter = sliderAdapter
        binding.nextBtn.setOnClickListener {
            if (binding.introSliderViewPager.currentItem + 1 < sliderAdapter.itemCount) {
                binding.introSliderViewPager.currentItem++
            } else {
                startActivity(Intent(applicationContext, LoginActivity::class.java))
                finish()
            }
        }
    }
}