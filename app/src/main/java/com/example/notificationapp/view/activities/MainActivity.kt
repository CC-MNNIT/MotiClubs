package com.example.notificationapp.view.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.notificationapp.R
import com.example.notificationapp.utils.Constants
import com.example.notificationapp.view.fragments.*
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mNavigationView: NavigationView
    private lateinit var mDrawerToggle: ActionBarDrawerToggle

    private lateinit var mProfileImage: ImageView
    private lateinit var mEditIcon: ImageView

    private lateinit var mUserNameTV: TextView
    private lateinit var mUserEmailTV: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setReferences()
        setListeners()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment()).commit()
    }

    private fun setListeners() {
        mDrawerToggle = ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close)
        mDrawerLayout.addDrawerListener(mDrawerToggle)
        mDrawerToggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mNavigationView.bringToFront()
        mNavigationView.setCheckedItem(R.id.home)

        mNavigationView.setNavigationItemSelectedListener { item: MenuItem ->
            if (item.itemId == R.id.logout) {
                logout()
                return@setNavigationItemSelectedListener true
            }
            supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container, when (item.itemId) {
                    R.id.notifications -> NotificationsFragment()
                    R.id.admin_panel -> AdminPanelFragment()
                    R.id.profile_section -> ProfileFragment()
                    R.id.about_us -> AboutUsFragment()
                    R.id.help -> HelpFragment()
                    else -> HomeFragment()
                }
            ).commit()
            mDrawerLayout.closeDrawer(GravityCompat.START)
            true
        }
        mEditIcon.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ProfileFragment()).commit()
            mDrawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    private fun setReferences() {
        mDrawerLayout = findViewById(R.id.drawer_layout)
        mNavigationView = findViewById(R.id.nav_view)

        val mHeaderView = mNavigationView.getHeaderView(0)
        mProfileImage = mHeaderView.findViewById(R.id.profilepic)
        mUserEmailTV = mHeaderView.findViewById(R.id.useremail)
        mUserNameTV = mHeaderView.findViewById(R.id.username)
        mEditIcon = mHeaderView.findViewById(R.id.btnedit)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (mDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START)
            return
        }
        super.onBackPressed()
    }

    private fun logout() {
        getSharedPreferences(Constants.SHARED_PREFERENCE, MODE_PRIVATE).edit().clear().apply();
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
    }
}