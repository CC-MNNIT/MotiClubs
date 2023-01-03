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
import android.widget.Toast;

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
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;
    View headerView;
    ImageView profileImage, editIcon;
    TextView userName, userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setReferences();
        setListeners();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
    }

    private void setListeners() {
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.bringToFront();
        navigationView.setCheckedItem(R.id.home);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    //TODO
                    case R.id.home:
                    {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                        break;
                    }
                    case R.id.notifications:
                    {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NotificationsFragemnt()).commit();
                        break;
                    }
                    case R.id.admin_panel:
                    {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AdminPanelFragment()).commit();
                        break;
                    }
                    case R.id.profile_section:
                    {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                        break;
                    }
                    case R.id.logout:
                    {
                        logout();
                        break;
                    }
                    case R.id.about_us:
                    {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AboutUsFragment()).commit();
                        break;
                    }
                    case R.id.help:
                    {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HelpFragment()).commit();
                        break;
                    }
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
    }

    private void setReferences() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);
        profileImage = headerView.findViewById(R.id.profilepic);
        userEmail = headerView.findViewById(R.id.useremail);
        userName = headerView.findViewById(R.id.username);
        editIcon = headerView.findViewById(R.id.btnedit);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SuspiciousIndentation")
    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else
        super.onBackPressed();
    }

    public void logout(){
        SharedPreferences preferences = getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE);
        preferences.edit().clear().apply();
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(i);
    }

}