package com.github.romainlogean.sdp_bootcamp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;

import static org.hamcrest.core.AllOf.allOf;

import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Rule
    public ActivityScenarioRule<MainActivity> testRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void ChangeActivityWithExtraTest(){

        Intents.init();

        onView(ViewMatchers.withId(R.id.mainName))
                .perform(clearText())
                .perform(typeText("Romain"))
                .perform(closeSoftKeyboard());

        onView(ViewMatchers.withId(R.id.mainGoButton)).perform(click());

        intended(allOf(hasExtra("NAME","Romain"),hasComponent(GreetingActivity.class.getName())));

        Intents.release();
    }
}
