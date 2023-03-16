package com.github.ybecker.epforuml

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.github.ybecker.epforuml.database.Database
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.MockDatabase
import com.github.ybecker.epforuml.database.Model
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    lateinit var toggle : ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    private lateinit var db : Database
    lateinit var bundle : Bundle

    //private var questionsList = mutableListOf<Model.Question>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // initialize DB to Mock
        DatabaseManager.useMockDatabase()

        drawerLayout = findViewById(R.id.drawer_layout)
        val navView : NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if(savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.frame_layout, HomeFragment()).commit()
        }


        // find a way of transfering content of db to fragments
       /* getQuestionsQuery()
        for (question in questionsList) {
            bundle.putString(question.questionId, question.questionText)
        }*/


        navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.nav_home -> replaceFragment(HomeFragment(), it.toString())
                R.id.nav_courses -> replaceFragment(CoursesFragment(), it.toString())
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

    /*fun getQuestionsQuery() {
        var courseSet = db.availableCourses()

        // reset questionsList for refresh
        questionsList = mutableListOf()

        for (course in courseSet) {
            questionsList.addAll(db.getCourseQuestions(course))
        }
    }*/
}