package com.github.ybecker.epforuml

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.github.ybecker.epforuml.notifications.FirebaseCouldMessagingAdapter
import com.github.ybecker.epforuml.account.AccountFragment
import com.github.ybecker.epforuml.account.AccountFragmentGuest
import com.github.ybecker.epforuml.chat.ChatHomeFragment
import com.github.ybecker.epforuml.chat.RealChatFragment
import com.github.ybecker.epforuml.database.DatabaseManager
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    companion object {
        lateinit var context: Context
    }

    lateinit var toggle : ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout

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

        if(savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.frame_layout, HomeFragment()).commit()
        }

        if( intent.extras?.getString("fragment").equals("NewQuestionFragment")) {
            supportFragmentManager.beginTransaction().replace(R.id.frame_layout, NewQuestionFragment()).commit()
        }
        if( intent.extras?.getString("fragment").equals("RealChat")) {
            supportFragmentManager.beginTransaction().replace(R.id.frame_layout, RealChatFragment()).commit()
        }
        if( intent.extras?.getString("fragment").equals("chatHome")) {
            supportFragmentManager.beginTransaction().replace(R.id.frame_layout, ChatHomeFragment()).commit()
        }
        // Remove it otherwise we might jump back to this fragment later
        intent.removeExtra("fragment")

        navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.nav_home -> replaceFragment(HomeFragment())
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
        supportFragmentManager.beginTransaction().replace(R.id.frame_layout, fragment).commit()
        drawerLayout.closeDrawers()
    }
}

