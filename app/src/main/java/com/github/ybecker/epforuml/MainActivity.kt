package com.github.ybecker.epforuml

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
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
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.Model
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    companion object {
        lateinit var context: Context

        private var connectivityManager : ConnectivityManager? = null
        fun isConnected() : Boolean {
            if (connectivityManager == null) { return false }

            return (connectivityManager?.getNetworkCapabilities(connectivityManager?.activeNetwork) != null)
        }
    }

    lateinit var toggle : ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout

    private var cache = ArrayList<Model.Question>()

    /**
     * List of all existing answers
     */
    private var answersCache = ArrayList<Model.Answer>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        context = applicationContext

        // get app connectivity
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // initialize DB to Mock
        //DatabaseManager.useMockDatabase()
        drawerLayout = findViewById(R.id.drawer_layout)
        val navView : NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // retrieve list of questions if any
        // TODO : optimize and only allow when logged in
        val newCache : ArrayList<Model.Question>? = intent.getParcelableArrayListExtra("savedQuestions")
        if (newCache != null) {
            cache = newCache
        }
        updateAnswersCacheIfConnected()

        // get retrieve name of fragment to display if any
        val fragment : String? = intent.extras?.getString("fragment")

        // TODO : change to switch (without savedInstanceState)
        if(savedInstanceState == null || fragment.equals("HomeFragment")) {
            replaceFragment(HomeFragment())
        }

        if(fragment.equals("NewQuestionFragment")) {
            replaceFragment(NewQuestionFragment(this))
        }
        if(fragment.equals("RealChat")) {
            replaceFragment(RealChatFragment())
        }
        if(fragment.equals("chatHome")) {
            replaceFragment(ChatHomeFragment())
        }

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
        val bundle = Bundle()
        // send cache to any of the fragments we are going to
        bundle.putParcelableArrayList("savedQuestions", cache)
        bundle.putParcelableArrayList("savedAnswers", answersCache)
        fragment.arguments = bundle

        supportFragmentManager.beginTransaction().replace(R.id.frame_layout, fragment).commit()
        drawerLayout.closeDrawers()
    }

    private fun updateAnswersCacheIfConnected() {
        if (isConnected()) {
            answersCache.clear()

            for (question in cache) {
                db.getQuestionAnswers(question.questionId).thenAccept { answerList ->
                    answersCache.addAll(answerList)
                }
            }
        }
    }
}

