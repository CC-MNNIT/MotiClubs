package com.example.notificationapp.view.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.notificationapp.Constants
import com.example.notificationapp.R
import com.example.notificationapp.app.UserInstance
import com.example.notificationapp.databinding.ActivityMainBinding
import com.example.notificationapp.view.fragments.*
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : AppCompatActivity() {

    private lateinit var mDrawerToggle: ActionBarDrawerToggle
    private lateinit var mProfileImage: CircleImageView
    private lateinit var mUserNameTV: TextView
    private lateinit var mUserEmailTV: TextView

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setReferences()
        setListeners()
        setValues()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment()).commit()
    }

    private fun setValues() {
        mUserNameTV.text = UserInstance.getName()
        mUserEmailTV.text = UserInstance.getEmail()

        val avatar = UserInstance.getAvatar()
        if (avatar.isEmpty()) return
        Picasso.get().load(avatar).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profile_icon)
            .into(mProfileImage, object : Callback {
                override fun onSuccess() {}

                override fun onError(e: Exception?) {
                    Picasso.get().load(avatar).placeholder(R.drawable.profile_icon).into(mProfileImage)
                }
            })
    }

    private fun setListeners() {
        mDrawerToggle =
            ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(mDrawerToggle)
        mDrawerToggle.syncState()
        binding.navView.bringToFront()
        binding.navView.setCheckedItem(R.id.home)

        binding.menuBtn.setOnClickListener {
            if (!binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        binding.navView.setNavigationItemSelectedListener { item: MenuItem ->
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
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun setReferences() {
        binding.navView.menu.findItem(R.id.admin_panel).isVisible = UserInstance.isAdmin()

        val mHeaderView = binding.navView.getHeaderView(0)
        mProfileImage = mHeaderView.findViewById(R.id.profile_pic)
        mUserEmailTV = mHeaderView.findViewById(R.id.header_email)
        mUserNameTV = mHeaderView.findViewById(R.id.header_name)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (mDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            return
        }
        super.onBackPressed()
    }

    private fun logout() {
        getSharedPreferences(Constants.SHARED_PREFERENCE, MODE_PRIVATE).edit().clear().apply()
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
    }
}