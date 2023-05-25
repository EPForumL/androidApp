package com.github.ybecker.epforuml.util

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.github.ybecker.epforuml.R
import com.github.ybecker.epforuml.features.cache.SavedQuestionsFragment
import com.github.ybecker.epforuml.basicEntities.account.AccountFragment
import com.github.ybecker.epforuml.basicEntities.account.AccountFragmentGuest
import com.github.ybecker.epforuml.features.chat.ChatHomeFragment
import com.github.ybecker.epforuml.features.chat.RealChatFragment
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.Model
import com.github.ybecker.epforuml.basicEntities.questions.MyQuestionsFragment
import com.github.ybecker.epforuml.basicEntities.questions.NewQuestionFragment
import com.github.ybecker.epforuml.features.maps.MapsFragment
import com.github.ybecker.epforuml.structure.CoursesFragment
import com.github.ybecker.epforuml.structure.HomeFragment
import com.github.ybecker.epforuml.structure.SettingsFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {

    companion object {
        lateinit var context: Context

        private var connectivityManager : ConnectivityManager? = null
        fun isConnected() : Boolean {
            if (connectivityManager == null) { return false }

            return (connectivityManager?.getNetworkCapabilities(connectivityManager?.activeNetwork) != null)
        }


        /**
         * Saved question cache and answer cache to device
         */
        fun saveDataToDevice(questionCache: ArrayList<Model.Question>, answerCache: ArrayList<Model.Answer>) {
            val sharedQuestions : SharedPreferences = context.getSharedPreferences("QUESTIONS", MODE_PRIVATE)
            val sharedAnswers : SharedPreferences = context.getSharedPreferences("ANSWERS", MODE_PRIVATE)

            val questionsEditor = sharedQuestions.edit()
            val answersEditor = sharedAnswers.edit()

            val qGson = Gson()
            val aGson = Gson()

            val qJson = qGson.toJson(questionCache)
            val aJson = aGson.toJson(answerCache)

            questionsEditor.putString("questions", qJson)
            answersEditor.putString("answers", aJson)

            questionsEditor.apply()
            answersEditor.apply()
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
        FirebaseApp.initializeApp(this)
        context = applicationContext

        // get app connectivity
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        updateAnswersCacheIfConnected()

        // initialize DB to Mock
        //DatabaseManager.useMockDatabase()

        drawerLayout = findViewById(R.id.drawer_layout)
        val navView : NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // load all data from device
        loadDataFromDevice()

        /*
        // retrieve list of questions if any
        // TODO : optimize and only allow when logged in
        val newCache : ArrayList<Model.Question>? = intent.getParcelableArrayListExtra("savedQuestions")
        if (newCache != null) {
            cache = newCache
        }

        val newAnswersCache : ArrayList<Model.Answer>? = intent.getParcelableArrayListExtra("savedAnswers")
        if (newAnswersCache != null) {
            answersCache = newAnswersCache
        }

         */

        // get retrieve name of fragment to display if any
        val fragment : String? = intent.extras?.getString("fragment")

        // TODO : change to switch (without savedInstanceState)
        if(savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }

        when(fragment) {
            "HomeFragment" -> replaceFragment(HomeFragment())
            "NewQuestionFragment" -> replaceFragment(NewQuestionFragment())
            "SavedQuestionsFragment" -> replaceFragment(SavedQuestionsFragment())
            "RealChat" -> replaceFragment(RealChatFragment())
            "chatHome" -> replaceFragment(ChatHomeFragment())
            "MyQuestionsFragment" -> replaceFragment(MyQuestionsFragment())
            else -> {}
        }

        // Remove it otherwise we might jump back to this fragment later
        intent.removeExtra("fragment")

        navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.nav_home -> replaceFragmentAndClose(HomeFragment())
                R.id.nav_courses -> replaceFragmentAndClose(CoursesFragment())
                R.id.nav_my_questions -> replaceFragmentAndClose(MyQuestionsFragment())
                R.id.nav_saved_questions -> replaceFragmentAndClose(SavedQuestionsFragment())
                R.id.nav_account ->
                    if (DatabaseManager.user == null) {
                        replaceFragmentAndClose(AccountFragmentGuest())
                    } else {
                        replaceFragmentAndClose(AccountFragment())
                    }
                R.id.nav_settings -> replaceFragmentAndClose(SettingsFragment())
                R.id.nav_chat -> replaceFragmentAndClose(ChatHomeFragment())
                R.id.nav_map -> replaceFragment(MapsFragment())
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

    private fun replaceFragmentAndClose(fragment: Fragment) {
        replaceFragment(fragment)
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



    /**
     * Helper function for function below
     */
    inline fun <reified T> Gson.fromJson(json: String) = fromJson<T>(json, object: TypeToken<T>() {}.type)

    private fun loadDataFromDevice() {
        val sharedQuestions : SharedPreferences = context.getSharedPreferences("QUESTIONS", MODE_PRIVATE)
        val sharedAnswers : SharedPreferences = context.getSharedPreferences("ANSWERS", MODE_PRIVATE)

        val qGson = Gson()
        val aGson = Gson()

        val qJson =  sharedQuestions.getString("questions", null)
        val aJson =  sharedAnswers.getString("answers", null)

        if (qJson != null) {
            val cacheTmp = qGson.fromJson<ArrayList<Model.Question>>(qJson)
            if (cacheTmp != null) {
                cache = cacheTmp
            }
        }

        if (aJson != null) {
            val answerCacheTmp = aGson.fromJson<ArrayList<Model.Answer>>(aJson)
            if (answerCacheTmp != null) {
                answersCache = answerCacheTmp
            }
        }
    }
}

