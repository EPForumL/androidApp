package com.github.romainlogean.sdp_bootcamp;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class GreetingActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greeting);

        String name = getIntent().getStringExtra("NAME");
        TextView greetingTextView = findViewById(R.id.greeting_text);
        greetingTextView.setText("Welcome to EPForumL, " + name + " !");

        // create toolbar
        Toolbar toolbar = findViewById(R.id.toolbar); //Ignore red line errors
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.main_drawer_layout);
        NavigationView navigationView = findViewById(R.id.navOptions);
        navigationView.setNavigationItemSelectedListener(this);

        // enable toggling options
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // default sets to Forum Fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new ForumFragment())
                    .commit();
            navigationView.setCheckedItem(R.id.forumMenuItem);
        }
    }

    // TO DO
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.forumMenuItem:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new ForumFragment())
                        .commit();
                break;

            case R.id.chatMenuItem:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new ChatFragment())
                        .commit();
                break;

            case R.id.savedMenuItem:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new SavedFragment())
                        .commit();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}