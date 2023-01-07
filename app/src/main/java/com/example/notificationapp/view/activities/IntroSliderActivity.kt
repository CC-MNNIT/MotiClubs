package com.example.notificationapp.view.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.notificationapp.R
import com.example.notificationapp.adapters.IntroSliderAdapter
import com.example.notificationapp.data.network.IntroSlide
import com.example.notificationapp.databinding.ActivityIntroSliderBinding

class IntroSliderActivity : AppCompatActivity() {

    // Initializing required variables
    private lateinit var mIntroSliderAdapter: IntroSliderAdapter
    private val mIntroSlidesList: MutableList<IntroSlide> = ArrayList()

    private lateinit var binding: ActivityIntroSliderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroSliderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // hiding action bar
        supportActionBar?.hide()

        // adding data to intro slider data set
        mIntroSlidesList.add(
            IntroSlide(
                "welcome",
                "welcome",
                R.drawable.campus
            )
        )
        mIntroSlidesList.add(
            IntroSlide(
                "welcome",
                "welcome",
                R.drawable.campus
            )
        )
        mIntroSlidesList.add(
            IntroSlide(
                "welcome",
                "welcome",
                R.drawable.campus
            )
        )

        // creating adapter object
        mIntroSliderAdapter = IntroSliderAdapter(mIntroSlidesList)

        // setting adapter to the view
        binding.introSliderViewPager.adapter = mIntroSliderAdapter
        binding.nextBtn.setOnClickListener {
            if (binding.introSliderViewPager.currentItem + 1 < mIntroSliderAdapter.itemCount) {
                binding.introSliderViewPager.currentItem++
            } else {
                startActivity(Intent(applicationContext, SignUpActivity::class.java))
                finish()
            }
        }
    }
}