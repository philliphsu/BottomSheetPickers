package com.philliphsu.bottomsheetpickers;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;

import com.example.bottomsheetpickers.R;
import com.example.bottomsheetpickers.TextSwitcherActivity;
import com.philliphsu.bottomsheetpickers.view.LocaleModel;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
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

    private LocaleModel mLocaleModel;

    private boolean mIs24HourMode;

    @Before
    public void setup() {
        mLocaleModel = new LocaleModel(mActivityTestRule.getActivity());
        mIs24HourMode = DateFormat.is24HourFormat(mActivityTestRule.getActivity());
    }

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

    @Test
    public void verifyInitialViewEnabledStates() {
        openTimePicker();
        Espresso.onView(ViewMatchers.withId(R.id.bsp_input_time)).check(
                ViewAssertions.matches(ViewMatchers.withText("")));
        // Check that the am/pm view is set to the correct visibility.
        //
        // Rather than use the isDisplayed() matcher, which, on top of matching the view to a
        // View.VISIBLE state, matches the view to being drawn with visible bounds, we use
        // the withEffectiveVisibility() matcher to match only the former criterion.
        Espresso.onView(ViewMatchers.withId(R.id.bsp_input_ampm)).check(
                ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(mIs24HourMode ?
                        ViewMatchers.Visibility.GONE : ViewMatchers.Visibility.VISIBLE)));
        if (!mIs24HourMode) {
            Espresso.onView(ViewMatchers.withId(R.id.bsp_input_ampm)).check(
                    ViewAssertions.matches(isNthChildOf(
                            ViewMatchers.withId(R.id.bsp_input_time_container),
                            mLocaleModel.isAmPmWrittenBeforeTime() ? 0 : 1)));
        }
        Espresso.onView(ViewMatchers.withId(R.id.bsp_backspace)).check(
                matchesIsEnabled(false));
        // We can easily manually verify whether the divider is focused, so it's not worth the
        // trouble of writing a test.
        for (int i = 0; i < 10; i++) {
            Espresso.onView(withDigit(i)).check(matchesIsEnabled(mIs24HourMode || i > 0));
        }
        Espresso.onView(ViewMatchers.withId(R.id.bsp_text9)).check(matchesIsEnabled(false));
        Espresso.onView(ViewMatchers.withId(R.id.bsp_text11)).check(matchesIsEnabled(false));
        Espresso.onView(ViewMatchers.withText(android.R.string.ok)).check(matchesIsEnabled(false));
    }

    @Test
    public void clickNumberKey() {
        openTimePicker();
        Espresso.onView(withDigit(1)).perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withId(R.id.bsp_input_time)).check(
                ViewAssertions.matches(withDigit(1)));
    }

    private static void openTimePicker() {
        Espresso.onView(ViewMatchers.withId(R.id.button3)).perform(ViewActions.click());
    }

    /**
     * Helper method that wraps {@link ViewMatchers#withText(String) withText(String)}.
     *
     * @return A Matcher that matches a number key button by its text representation
     *         of {@code digit}.
     */
    private static Matcher<View> withDigit(int digit) {
        // TODO: When we're comfortable with the APIs, we can statically import them and
        // make direct calls to these methods and cut down on the verbosity, instead of
        // writing helper methods that wrap these APIs.
        return ViewMatchers.withText(text(digit));
    }

    // TODO: See if we can use ButtonTextModel#text() instead. Currently, it is package private.
    private static String text(int digit) {
        return String.format("%d", digit);
    }

    /**
     * @param enabled Whether the view should be matched to be enabled or not.
     * @return A {@link ViewAssertion} that asserts that a view should be matched
     *         to be enabled or disabled.
     */
    private static ViewAssertion matchesIsEnabled(boolean enabled) {
        // TODO: When we're comfortable with the APIs, we can statically import them and
        // make direct calls to these methods and cut down on the verbosity, instead of
        // writing helper methods that wrap these APIs.
        return ViewAssertions.matches(enabled ? ViewMatchers.isEnabled() : Matchers.not(ViewMatchers.isEnabled()));
    }

    /**
     * Returns a matcher that matches a {@link View} that is a child of the described parent
     * at the specified index.
     *
     * @param parentMatcher A matcher that describes the view's parent.
     * @param childIndex The index of the view at which it is a child of the described parent.
     */
    private static Matcher<View> isNthChildOf(final Matcher<View> parentMatcher, final int childIndex) {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("is child at index "+childIndex+" of view matched by parentMatcher: ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewGroup parent = (ViewGroup) view.getParent();
                return parentMatcher.matches(parent) && view.equals(parent.getChildAt(childIndex));
            }
        };
    }
}
