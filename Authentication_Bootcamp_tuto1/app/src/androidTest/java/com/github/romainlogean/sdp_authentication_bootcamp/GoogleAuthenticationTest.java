package com.github.romainlogean.sdp_authentication_bootcamp;

import static android.app.Activity.RESULT_OK;

import com.github.romainlogean.sdp_authentication_bootcamp.MainActivity.*;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class GoogleAuthenticationTest {
    private ActivityScenario<MainActivity> scenario;

    @Rule
    public ActivityScenarioRule<MainActivity> testRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void ConnectLaunchGoogleConnection() throws Exception {
        onView(withId(R.id.login_button)).perform(click());

        onView(withText("Continue with Google"));
    }


    @Test
    public void testDisconnectButton() {

        onView(withId(R.id.logout_button)).perform(click());
        assertNull(FirebaseAuth.getInstance().getCurrentUser());
        onView(ViewMatchers.withId(R.id.connexion_state)).check(matches(withText("Disconnected")));
    }
}