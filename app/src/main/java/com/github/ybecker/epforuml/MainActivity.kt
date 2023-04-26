package com.github.ybecker.epforuml

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.github.ybecker.epforuml.notifications.FirebaseCouldMessagingAdapter
import com.github.ybecker.epforuml.account.AccountFragment
import com.github.ybecker.epforuml.account.AccountFragmentGuest
import com.github.ybecker.epforuml.chat.ChatHomeFragment
import com.github.ybecker.epforuml.chat.RealChatFragment
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.Model
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    companion object {
        lateinit var context: Context
    }

    lateinit var toggle : ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout

    private var cache = ArrayList<Model.Question>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        context = applicationContext

        // initialize DB to Mock
        //DatabaseManager.useMockDatabase()
        drawerLayout = findViewById(R.id.drawer_layout)
        val navView : NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // retrieve list of questions if any
        val newCache : ArrayList<Model.Question>? = intent.getParcelableArrayListExtra("savedQuestions")
        if (newCache != null) {
            cache = newCache
        }

        if(savedInstanceState == null) {
            replaceFragment(HomeFragment(this))
        }

        if( intent.extras?.getString("fragment").equals("NewQuestionFragment")) {
            replaceFragment(NewQuestionFragment(this))
        }
        if( intent.extras?.getString("fragment").equals("RealChat")) {
            replaceFragment(RealChatFragment())
        }
        if( intent.extras?.getString("fragment").equals("chatHome")) {
            replaceFragment(ChatHomeFragment())
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
                R.id.nav_chat -> replaceFragment(ChatHomeFragment())
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
        // send cache to any of the fragments we are going to
        bundle.putParcelableArrayList("savedQuestions", cache)
        fragment.arguments = bundle

        supportFragmentManager.beginTransaction().replace(R.id.frame_layout, fragment).commit()
        drawerLayout.closeDrawers()
    }
}

