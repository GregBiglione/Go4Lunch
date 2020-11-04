package com.greg.go4lunch;

import android.content.Context;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withChild;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static java.lang.Thread.sleep;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class FragmentInstrumentedTest {

    private MainActivity mMainActivity;

    @Rule
    public ActivityTestRule<MainActivity> mainActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp(){
        mMainActivity = mainActivityTestRule.getActivity();
        assertThat(mMainActivity, notNullValue());
    }

    public void delayer(){
        try {
            sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void goToAllFragment_andCheckEachFragment_isDisplayed(){
        delayer();
        onView(withId(R.id.nav_workmates))
                .perform(click());
        onView(withId(R.id.workmates_fragment))
                .check(matches(isDisplayed()));

        delayer();
        onView(withId(R.id.nav_list))
                .perform(click());
        onView(withId(R.id.list_fragment))
                .check(matches(isDisplayed()));

        delayer();
        onView(withId(R.id.nav_maps))
                .perform(click());
        onView(withId(R.id.home_fragment))
                .check(matches(isDisplayed()));
    }
}
