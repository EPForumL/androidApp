package com.github.ybecker.epforuml

import android.os.Bundle
import android.provider.ContactsContract.RawContacts.Data
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.github.ybecker.epforuml.account.AccountFragment
import com.github.ybecker.epforuml.account.AccountFragmentGuest
import com.github.ybecker.epforuml.cache.SavedQuestionsCache
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.Model
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DatabaseReference

class MainActivity : AppCompatActivity() {

    lateinit var toggle : ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout

    private lateinit var user : Model.User

    private lateinit var cache : SavedQuestionsCache
    private var bundle = Bundle()

    //private lateinit var reference : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // initialize DB to Mock
        //DatabaseManager.useMockDatabase()

        user = DatabaseManager.user ?: Model.User()

        // retrieve saved questions if any
        cache = savedInstanceState?.getParcelable("savedQuestions") ?: SavedQuestionsCache()

        // enable navigation drawer
        drawerLayout = findViewById(R.id.drawer_layout)
        val navView : NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        // set value to transmit to Fragments
        // TODO : check if actually transmits or if extra Bundle is needed
        bundle.putParcelable("savedQuestions", cache)


        if(savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.frame_layout, HomeFragment(this)).commit()
        }

        if(intent.extras?.getString("fragment").equals("NewQuestionFragment")) {
            supportFragmentManager.beginTransaction().replace(R.id.frame_layout, NewQuestionFragment(this)).commit()
        }

        navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.nav_home -> replaceFragment(HomeFragment(this))
                R.id.nav_courses -> replaceFragment(CoursesFragment())
                R.id.nav_my_questions -> replaceFragment(MyQuestionsFragment())
                R.id.nav_saved_questions -> replaceFragment(SavedQuestionsFragment())
                R.id.nav_account ->
                    if (DatabaseManager.user == null) {
                        replaceFragment(AccountFragmentGuest())
                    } else {
                        replaceFragment(AccountFragment())
                    }
                R.id.nav_settings -> replaceFragment(SettingsFragment())
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

    fun replaceFragment(fragment: Fragment) {
        fragment.arguments = bundle
        supportFragmentManager.beginTransaction().replace(R.id.frame_layout, fragment).commit()
        drawerLayout.closeDrawers()
    }
}

