package com.github.romainlogean.sdp_bootcamp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ForumFragmentTest {
    /*@Rule
    public final ActivityScenarioRule<ForumFragment> activityRule =
            new ActivityScenarioRule(ForumFragment.class);*/

    /*@Test
    public void forumFragmentIsDisplayed() {
        FragmentScenario scenario = FragmentScenario.launch(ForumFragment.class);
        //scenario.moveToState(Lifecycle.State.STARTED);

        //onView(ViewMatchers.withId(R.id.forumText)).check(matches(withText("Forum")));

        onView(ViewMatchers.withId(R.id.forumText)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        scenario.close();
    }*/
}
