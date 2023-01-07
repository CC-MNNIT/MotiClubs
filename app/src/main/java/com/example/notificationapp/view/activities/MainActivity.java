package com.example.notificationapp.view.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.notificationapp.R;
import com.example.notificationapp.utils.Constants;
import com.example.notificationapp.view.fragments.AboutUsFragment;
import com.example.notificationapp.view.fragments.AdminPanelFragment;
import com.example.notificationapp.view.fragments.HelpFragment;
import com.example.notificationapp.view.fragments.HomeFragment;
import com.example.notificationapp.view.fragments.NotificationsFragemnt;
import com.example.notificationapp.view.fragments.ProfileFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle mDrawerToggle;
    private ImageView mProfileImage, mEditIcon;
    private TextView mUserNameTV, mUserEmailTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setReferences();
        setListeners();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
    }

    private void setListeners() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mNavigationView.bringToFront();
        mNavigationView.setCheckedItem(R.id.home);
        mNavigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                //TODO
                case R.id.home: {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                    break;
                }
                case R.id.notifications: {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NotificationsFragemnt()).commit();
                    break;
                }
                case R.id.admin_panel: {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AdminPanelFragment()).commit();
                    break;
                }
                case R.id.profile_section: {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                    break;
                }
                case R.id.logout: {
                    logout();
                    break;
                }
                case R.id.about_us: {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AboutUsFragment()).commit();
                    break;
                }
                case R.id.help: {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HelpFragment()).commit();
                    break;
                }
            }
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
        mEditIcon.setOnClickListener(view -> {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
            mDrawerLayout.closeDrawer(GravityCompat.START);
        });
    }

    private void setReferences() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
        View mHeaderView = mNavigationView.getHeaderView(0);
        mProfileImage = mHeaderView.findViewById(R.id.profilepic);
        mUserEmailTV = mHeaderView.findViewById(R.id.useremail);
        mUserNameTV = mHeaderView.findViewById(R.id.username);
        mEditIcon = mHeaderView.findViewById(R.id.btnedit);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SuspiciousIndentation")
    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        super.onBackPressed();
    }

    public void logout() {
        SharedPreferences preferences = getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE);
        preferences.edit().clear().apply();
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(i);
    }
}