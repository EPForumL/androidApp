package com.github.romainlogean.sdp_firebase_bootcamp;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.After;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import java.util.Map;

//the
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
public class FireDatabaseTests {

    private static final String PHONE_EXAMPLE = "0790000000";
    private static final String EMAIL_EXAMPLE = "setAndGetTest@gmail.com";

    @Rule
    public ActivityScenarioRule<MainActivity> testRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void setAndGetEmulatorTest() {
        //To run this test make sur to install firebase database emulator
        //and run "firebase emulators:start --only database"

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        db.useEmulator("127.0.0.1", 8080);

        onView(withId(R.id.phone_textview))
                .perform(clearText())
                .perform(typeText(PHONE_EXAMPLE))
                .perform(closeSoftKeyboard());

        onView(withId(R.id.email_textview))
                .perform(clearText())
                .perform(typeText(EMAIL_EXAMPLE))
                .perform(closeSoftKeyboard());

        onView(withId(R.id.button_set)).perform(click());

        onView(withId(R.id.email_textview)).perform(clearText());

        onView(withId(R.id.button_get)).perform(click());

        onView(withId(R.id.email_textview)).check(matches(withText(EMAIL_EXAMPLE)));

        db.getReference().setValue(null);
    }

    @Test
    public void setAndGetMockDbTest() {
        DatabaseManager.useMockDatabase();

        onView(withId(R.id.phone_textview))
                .perform(clearText())
                .perform(typeText(PHONE_EXAMPLE))
                .perform(closeSoftKeyboard());

        onView(withId(R.id.email_textview))
                .perform(clearText())
                .perform(typeText(EMAIL_EXAMPLE))
                .perform(closeSoftKeyboard());

        onView(withId(R.id.button_set)).perform(click());

        onView(withId(R.id.email_textview)).perform(clearText());

        onView(withId(R.id.button_get)).perform(click());

        onView(withId(R.id.email_textview)).check(matches(withText(EMAIL_EXAMPLE)));
    }
}