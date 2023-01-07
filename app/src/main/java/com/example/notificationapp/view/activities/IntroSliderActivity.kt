package com.example.notificationapp.view.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.notificationapp.R
import com.example.notificationapp.adapters.IntroSliderAdapter
import com.example.notificationapp.data.network.IntroSlide
import com.google.android.material.button.MaterialButton
import java.util.*

class IntroSliderActivity : AppCompatActivity() {

    // Initializing required variables
    private lateinit var mIntroSliderAdapter: IntroSliderAdapter
    private lateinit var mIntroSliderViewPager: ViewPager2
    private val mIntroSlidesList: MutableList<IntroSlide> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro_slider)
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
        mIntroSliderViewPager = findViewById<View>(R.id.introSliderViewPager) as ViewPager2
        mIntroSliderViewPager.adapter = mIntroSliderAdapter
        val mNextBtn = findViewById<MaterialButton>(R.id.next_btn)
        mNextBtn.setOnClickListener {
            if (mIntroSliderViewPager.currentItem + 1 < mIntroSliderAdapter.itemCount) {
                mIntroSliderViewPager.currentItem = mIntroSliderViewPager.currentItem + 1
            } else {
                startActivity(Intent(applicationContext, SignUpActivity::class.java))
                finish()
            }
        }
    }
}