package com.github.ybecker.epforuml.chat

import android.app.Activity
import android.content.Intent
import android.widget.ListView
import androidx.core.view.size
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.ybecker.epforuml.R
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.Model
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchActivityTest {
    private lateinit var host : Model.User
    private lateinit var extern : Model.User
    private lateinit var scenario : ActivityScenario<Activity>

    @Before
    fun setTestsUp(){
        DatabaseManager.useMockDatabase()
        Firebase.auth.signOut()
        //set up database
        host = db.addUser("0", "HostUser", "testEmail").get()
        extern = db.addUser("1", "ExternUser", "testEmail").get()
        DatabaseManager.user = host
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            SearchActivity::class.java)
        scenario = ActivityScenario.launch(intent)

    }

    @Test
    fun testSearchViewDisplayed() {
        onView(withId(R.id.searchView)).check(matches(isDisplayed()))
    }

    @Test
    fun testListViewDisplayed() {
        onView(withId(R.id.listView)).check(matches(isDisplayed()))
    }

    @Test
    fun testListViewContainsDataFromDatabase() {
        onView(withText("ExternUser")).check(matches(isDisplayed()))
        onView(withText("HostUser")).check(matches(isDisplayed()))
    }

    @Test
    fun testFilterListView() {
        val searchText = "Ext"
        onView(withId(R.id.searchView)).perform(typeText(searchText))
        onView(withText("ExternUser")).check(matches(isDisplayed()))
    }
    @Test
    fun backToHomeIsCorrect() {

        onView(withId(R.id.back_to_home_button)).perform(ViewActions.click())

        onView(withId(R.id.recycler_chat_home)).check(matches(isDisplayed()))
    }
    @Test
    fun testNoResultsFound() {
        val searchText = "non-existing-user"
        onView(withId(R.id.searchView)).perform(typeText(searchText))
        scenario.onActivity { activity ->
            val view: ListView = activity.findViewById(R.id.listView)
            assertThat(view.size, `is`(0))
        }

    }


  @Test
    fun correctSearchLeadsToChat(){

        val searchText = "ExternUser"
        onView(withId(R.id.searchView)).perform(typeText(searchText))
        onView(withParent(withId(R.id.listView)).also { withText("ExternUser")}).perform(click())
        onView(withId(R.id.title_chat)).check(matches(withText("ExternUser")))

    }

    @After
    fun tearDown(){
        scenario.close()
    }

}
