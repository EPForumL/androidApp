package com.github.romainlogean.sdp_bootcamp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.mainGoButton);
        button.setOnClickListener(this::greet);

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

    public void greet(View view){
        Intent intent = new Intent(view.getContext(), GreetingActivity.class);
        TextView nameTV = findViewById(R.id.mainName);
        String name = nameTV.getText().toString();
        intent.putExtra("NAME", name);
        view.getContext().startActivity(intent);
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