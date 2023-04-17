package com.github.ybecker.epforuml

import android.os.Bundle
import android.provider.ContactsContract.RawContacts.Data
import android.view.MenuItem
import android.widget.TextView
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

    private var cache = ArrayList<Model.Question>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // initialize DB to Mock
        //DatabaseManager.useMockDatabase()

        user = DatabaseManager.user ?: Model.User()

        // enable navigation drawer
        drawerLayout = findViewById(R.id.drawer_layout)
        val navView : NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val newCache : ArrayList<Model.Question>? = intent.getParcelableArrayListExtra("savedQuestions")
        if (newCache != null) {
            cache = newCache
        }

        if(savedInstanceState == null) {
            replaceFragment(HomeFragment(this))
        }

        // TODO change to replace
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
        val bundle = Bundle()
        bundle.putParcelableArrayList("savedQuestions", cache)
        //sendCache(bundle)
        fragment.arguments = bundle

        supportFragmentManager.beginTransaction().replace(R.id.frame_layout, fragment).commit()
        drawerLayout.closeDrawers()
    }
/*
    fun sendCache(bundle : Bundle) {
        val list = cache.toListOfQuestions()

        for (q in list) {
            bundle.putParcelable(q.questionId, q)
        }
    }

 */

    // faire en sorte de n'envoyer que des booleans (true/false pour save) entre les fragment.
    fun updateCache() {

    }
}

