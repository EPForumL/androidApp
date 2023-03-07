package com.github.ybecker.epforuml

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.compose.setContent
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    lateinit var toggle : ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navView : NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // how to make the toggle open the drawer ?
        /*toggle.setToolbarNavigationClickListener {
            drawerLayout.openDrawer(navView)
        }*/

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if(savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.frame_layout, HomeFragment()).commit()
        }


        navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.nav_home -> replaceFragment(HomeFragment(), it.toString())
                R.id.nav_my_questions -> replaceFragment(MyQuestionsFragment(), it.toString())
                R.id.nav_saved_questions -> replaceFragment(SavedQuestionsFragment(), it.toString())
                R.id.nav_account -> replaceFragment(AccountFragment(), it.toString())
                R.id.nav_settings -> replaceFragment(SettingsFragment(), it.toString())
            }

            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun replaceFragment(fragment: Fragment, title : String) {
        supportFragmentManager.beginTransaction().replace(R.id.frame_layout, fragment).commit()
        drawerLayout.closeDrawers()
        setTitle(title)
    }

    /**
     * Creates the input field for the name and the button to transition to the greeting activity
     */
    @OptIn(ExperimentalMaterial3Api::class)
    @Preview
    @Composable
    fun NameAndButton() {
        // We can't use "this" inside the onClick lambda as we would not get the same context
        val context = this

        // Used to align vertically the window components
        Column(content = {
            // This is a standard way to update the TextField when the contents are modified
            var name by remember { mutableStateOf("Your Name") }

            TextField(
                value = name,
                onValueChange = { str -> name = str }
            )

            Button(onClick = {
                val intent = Intent(context, GreetingActivity::class.java)
                // We pass the name entered by the user to the greeting activity
                intent.putExtra("name", name)
                startActivity(intent)
            }) {
                Text("Click me")
            }
        })
    }
}