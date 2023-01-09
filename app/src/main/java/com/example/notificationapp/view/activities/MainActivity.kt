package com.example.notificationapp.view.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.notificationapp.Constants
import com.example.notificationapp.R
import com.example.notificationapp.data.network.UserResponse
import com.example.notificationapp.data.network.api.RetrofitAccessObject
import com.example.notificationapp.databinding.ActivityMainBinding
import com.example.notificationapp.view.fragments.*
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var mDrawerToggle: ActionBarDrawerToggle

    private lateinit var mProfileImage: ImageView
    private lateinit var mEditIcon: ImageView

    private lateinit var mUserNameTV: TextView
    private lateinit var mUserEmailTV: TextView

    private lateinit var binding: ActivityMainBinding
    private var user: UserResponse? = null
    private lateinit var preferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferences = getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE)
        getUserData()
        Log.d("Test2", user.toString())
        updateToken()
        setReferences()
        setListeners()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment())
            .commit()

    }

    private fun setListeners() {
        mDrawerToggle =
            ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(mDrawerToggle)
        mDrawerToggle.syncState()
        binding.navView.bringToFront()
        binding.navView.setCheckedItem(R.id.home)

        binding.toolbar.setNavigationOnClickListener {
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
        mEditIcon.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment()).commit()
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    private fun setReferences() {
        val mHeaderView = binding.navView.getHeaderView(0)
        mProfileImage = mHeaderView.findViewById(R.id.profile_pic)
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

    private fun getUserData() {

        RetrofitAccessObject.getRetrofitAccessObject()
            .getUserData(preferences.getString(Constants.TOKEN, ""))
            .enqueue(object : Callback<UserResponse?> {
                override fun onResponse(
                    call: Call<UserResponse?>,
                    response: Response<UserResponse?>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        user = response.body()
                        Log.d("Test1", user.toString())
                        handleAdminPanelVisibility()

                    }
                }
                override fun onFailure(call: Call<UserResponse?>, t: Throwable) {}
            })
    }

    private fun updateToken(){
        FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.addOnSuccessListener {result ->
            var idToken = result.token
            preferences.edit().putString(Constants.TOKEN, idToken).apply()
        }
    }

    private fun handleAdminPanelVisibility(){
        if(user != null && user!!.admin.isNotEmpty()){
            binding.navView.menu.findItem(R.id.admin_panel).setVisible(true)
        }else{
            if(user?.admin?.isEmpty()  == null)
            binding.navView.menu.findItem(R.id.admin_panel).setVisible(false)

        }
    }
}