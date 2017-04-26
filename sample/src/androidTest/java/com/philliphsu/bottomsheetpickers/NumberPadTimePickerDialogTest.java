package com.philliphsu.bottomsheetpickers;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.bottomsheetpickers.R;
import com.example.bottomsheetpickers.TextSwitcherActivity;

import org.hamcrest.Matcher;
import org.hamcrest.core.StringContains;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class NumberPadTimePickerDialogTest {
    private static final String TIME_PICKER_VIEW_CLASS_NAME = "NumberPadTimePicker";

    /**
     * {@link ActivityTestRule} is a JUnit {@link Rule @Rule} to launch your activity under test.
     *
     * <p>
     * Rules are interceptors which are executed for each test method and are important building
     * blocks of Junit tests.
     *
     * <p>
     * The annotated Activity will be launched before each annotated @Test and before any annotated
     * {@link Before @Before} methods. The Activity is automatically terminated after the test is
     * completed and all {@link After @After} methods are finished.
     */
    @Rule
    public ActivityTestRule<TextSwitcherActivity> mActivityTestRule =
            new ActivityTestRule<>(TextSwitcherActivity.class);

    @Test
    public void clickButton_opensTimePickerDialog() {
        // Specifies a view with the given id that we want to interact with.
        ViewInteraction viewInteraction = Espresso.onView(ViewMatchers.withId(R.id.button3));
        // Interact with the view by performing a ViewAction.
        viewInteraction.perform(ViewActions.click());

        // Obtain a Matcher that will allow us to determine if a string being examined contains
        // the specified string. Where does the string being examined come from? Espresso handles
        // that for us when we intend to search for this specified string.
        Matcher<String> classNameMatcher = StringContains.containsString(TIME_PICKER_VIEW_CLASS_NAME);
        // Specifies a view with the given class name that we want to interact with.
        viewInteraction = Espresso.onView(ViewMatchers.withClassName(classNameMatcher));
        // Specifies an assertion we want to test. Here, we convert a ViewMatcher that determines if
        // a view is displayed into a ViewAssertion.
        ViewAssertion assertion = ViewAssertions.matches(ViewMatchers.isDisplayed());
        // Interact with the view by checking the assertion.
        viewInteraction.check(assertion);
    }
}
