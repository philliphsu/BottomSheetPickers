package com.philliphsu.bottomsheetpickers;

import android.provider.Settings;
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
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class NumberPadTimePickerDialogTest {
    private static final List<TestCase> MODE_12HR_TESTS_1_TO_9 = new ArrayList<>(9);
    private static final List<TestCase> MODE_24HR_TESTS_0_TO_9 = new ArrayList<>(10);
    private static final List<TestCase> MODE_12HR_TESTS_10_TO_95 = new ArrayList<>(54);
    private static final List<TestCase> MODE_24HR_TESTS_00_TO_95 = new ArrayList<>(65);

//    // TODO
//    private static final List<TestCase> MODE_24HR_TESTS_000_TO_959 = new ArrayList<>();
//    // TODO
//    private static final List<TestCase> MODE_24HR_TESTS_0000_TO_2359 = new ArrayList<>();

    static {
        build_Mode12Hr_Tests_1_to_9();
        build_Mode24Hr_Tests_0_to_9();
        build_Mode12Hr_Tests_10_to_95();
        build_Mode24Hr_Tests_00_to_95();
    }

    private static void build_Mode12Hr_Tests_1_to_9() {
        for (int i = 1; i <= 9; i++) {
            MODE_12HR_TESTS_1_TO_9.add(new TestCase.Builder(array(i), false)
                    .numberKeysEnabled(0, 6 /* 1[0-2]:... or i:[0-5]... */)
                    .backspaceEnabled(true)
                    .headerDisplayFocused(true)
                    .altKeysEnabled(true)
                    .okButtonEnabled(false)
                    .timeDisplay(text(i))
                    .build());
        }
    }

    private static void build_Mode24Hr_Tests_0_to_9() {
        for (int i = 0; i <= 9; i++) {
            TestCase.Builder builder = new TestCase.Builder(array(i), true)
                    .backspaceEnabled(true)
                    .headerDisplayFocused(true)
                    .altKeysEnabled(true)
                    .okButtonEnabled(false)
                    .timeDisplay(text(i));
            if (i <= 1) {
                builder.numberKeysEnabled(0, 10 /* i[0-9]:... or i:[0-5]... */);
            } else {
                builder.numberKeysEnabled(0, 6 /* 2[0-3]:... or i:[0-5]... */);
            }
            MODE_24HR_TESTS_0_TO_9.add(builder.build());
        }
    }

    private static void build_Mode12Hr_Tests_10_to_95() {
        for (int i = 10; i <= 95; i++) {
            if (i % 10 > 5) continue;
            TestCase test = new TestCase.Builder(array(i / 10, i % 10), false)
                    .numberKeysEnabled(0, 10)
                    .backspaceEnabled(true)
                    .headerDisplayFocused(true)
                    .altKeysEnabled(i >= 10 && i <= 12)
                    .okButtonEnabled(false)
                    .timeDisplay(String.format("%d", i) /* TODO: Pull formatting logic from
                    Presenter impl. into its own class. Then format the current sequence of
                    digits. */)
                    .build();
            MODE_12HR_TESTS_10_TO_95.add(test);
        }
    }

    private static void build_Mode24Hr_Tests_00_to_95() {
        for (int i = 0; i <= 95; i++) {
            if (i % 10 > 5 && i > 25) continue;
            TestCase test = new TestCase.Builder(array(i / 10, i % 10), true)
                    .numberKeysEnabled(0, (i % 10 > 5) ? 6 : 10 /* (0-1)(6-9):[0-5] or (i_1):(i_2)[0-9]*/)
                    .backspaceEnabled(true)
                    .headerDisplayFocused(true)
                    .altKeysEnabled(i >= 0 && i <= 23)
                    .okButtonEnabled(false)
                    .timeDisplay(String.format("%02d", i) /* TODO: Pull formatting logic from
                    Presenter impl. into its own class. Then format the current sequence of
                    digits. */)
                    .build();
            MODE_24HR_TESTS_00_TO_95.add(test);
        }
    }

    private static int[] array(int... a) {
        return a == null ? new int[0] : a;
    }

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

    // Used to restore the device's time format at the end of testing.
    private boolean mInitiallyIn24HourMode;

    @Before
    public void setup() {
        mLocaleModel = new LocaleModel(mActivityTestRule.getActivity());
        mInitiallyIn24HourMode = DateFormat.is24HourFormat(mActivityTestRule.getActivity());
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
                ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(mInitiallyIn24HourMode ?
                        ViewMatchers.Visibility.GONE : ViewMatchers.Visibility.VISIBLE)));
        if (!mInitiallyIn24HourMode) {
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
            Espresso.onView(withDigit(i)).check(matchesIsEnabled(mInitiallyIn24HourMode || i > 0));
        }
        Espresso.onView(ViewMatchers.withId(R.id.bsp_text9)).check(matchesIsEnabled(false));
        Espresso.onView(ViewMatchers.withId(R.id.bsp_text11)).check(matchesIsEnabled(false));
        Espresso.onView(ViewMatchers.withText(android.R.string.ok)).check(matchesIsEnabled(false));
    }

    @Test
    public void mode12Hr_verifyViewEnabledStates_Input_1_to_9() {
        initializeTimePicker(false);
        verifyViewEnabledStates(MODE_12HR_TESTS_1_TO_9);
    }

    @Test
    public void mode24Hr_verifyViewEnabledStates_Input_0_to_9() {
        initializeTimePicker(true);
        verifyViewEnabledStates(MODE_24HR_TESTS_0_TO_9);
    }

    @Test
    public void mode12Hr_verifyViewEnabledStates_Input_10_to_95() {
        initializeTimePicker(false);
        verifyViewEnabledStates(MODE_12HR_TESTS_10_TO_95);
    }

    @Test
    public void mode24Hr_verifyViewEnabledStates_Input_00_to_95() {
        initializeTimePicker(true);
        verifyViewEnabledStates(MODE_24HR_TESTS_00_TO_95);
    }

    @After
    public void resetDeviceTimeFormat() {
        setDeviceTo24HourMode(mInitiallyIn24HourMode);
    }

    private void setDeviceTo24HourMode(boolean use24HourMode) {
        Settings.System.putString(mActivityTestRule.getActivity().getContentResolver(),
                Settings.System.TIME_12_24, use24HourMode ? "24" : "12");
    }

    private void initializeTimePicker(boolean use24HourMode) {
        setDeviceTo24HourMode(use24HourMode);
        openTimePicker();
        if (!use24HourMode) {
            // Check that '0' button is disabled.
            Espresso.onView(ViewMatchers.withId(R.id.bsp_text10)).check(matchesIsEnabled(false));
        }
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

    private static ViewInteraction[] getButtonInteractions() {
        ViewInteraction[] buttonsInteractions = new ViewInteraction[10];
        // We cannot rely on the withDigit() matcher to retrieve these because,
        // after performing a click on a button, the time display will update to
        // take on that button's digit text, and so withDigit() will return a matcher
        // that matches multiple views with that digit text: the button
        // itself and the time display. This will prevent us from performing
        // validation on the same ViewInteractions later.
        buttonsInteractions[0] = Espresso.onView(ViewMatchers.withId(R.id.bsp_text10));
        buttonsInteractions[1] = Espresso.onView(ViewMatchers.withId(R.id.bsp_text0));
        buttonsInteractions[2] = Espresso.onView(ViewMatchers.withId(R.id.bsp_text1));
        buttonsInteractions[3] = Espresso.onView(ViewMatchers.withId(R.id.bsp_text2));
        buttonsInteractions[4] = Espresso.onView(ViewMatchers.withId(R.id.bsp_text3));
        buttonsInteractions[5] = Espresso.onView(ViewMatchers.withId(R.id.bsp_text4));
        buttonsInteractions[6] = Espresso.onView(ViewMatchers.withId(R.id.bsp_text5));
        buttonsInteractions[7] = Espresso.onView(ViewMatchers.withId(R.id.bsp_text6));
        buttonsInteractions[8] = Espresso.onView(ViewMatchers.withId(R.id.bsp_text7));
        buttonsInteractions[9] = Espresso.onView(ViewMatchers.withId(R.id.bsp_text8));
        return buttonsInteractions;
    }

    private static ViewInteraction[] getAltButtonInteractions() {
        ViewInteraction[] buttonsInteractions = new ViewInteraction[2];
        buttonsInteractions[0] = Espresso.onView(ViewMatchers.withId(R.id.bsp_text9));
        buttonsInteractions[1] = Espresso.onView(ViewMatchers.withId(R.id.bsp_text11));
        return buttonsInteractions;
    }

    private static void verifyViewEnabledStates(List<TestCase> testSuite) {
        for (TestCase test : testSuite) {
            verifyViewEnabledStates(test);
        }
    }

    private static void verifyViewEnabledStates(TestCase test) {
        ViewInteraction[] buttonsInteractions = getButtonInteractions();
        ViewInteraction[] altButtonsInteractions = getAltButtonInteractions();
        for (int digit : test.sequence) {
            buttonsInteractions[digit]
                    .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
                    .perform(ViewActions.click());
        }
        for (int i = 0; i < 10; i++) {
            buttonsInteractions[i].check(matchesIsEnabled(
                    i >= test.numberKeysEnabledStart && i < test.numberKeysEnabledEnd));
            altButtonsInteractions[0].check(matchesIsEnabled(test.leftAltKeyEnabled));
            altButtonsInteractions[1].check(matchesIsEnabled(test.rightAltKeyEnabled));
        }

        ViewInteraction backspaceInteraction = Espresso.onView(
                ViewMatchers.withId(R.id.bsp_backspace));
        // Reset after each iteration by backspacing on the button just clicked.
        for (int digit : test.sequence) {
            backspaceInteraction.check(matchesIsEnabled(true)).perform(ViewActions.click());
        }
        backspaceInteraction.check(matchesIsEnabled(false));
    }

    private static final class TestCase {
        final int[] sequence;
        final boolean ampmState;

        final int numberKeysEnabledStart;
        final int numberKeysEnabledEnd;
        final boolean backspaceEnabled;
        final boolean headerDisplayFocused;
        final boolean leftAltKeyEnabled;
        final boolean rightAltKeyEnabled;
        final boolean okButtonEnabled;
        final CharSequence timeDisplay;
        final CharSequence ampmDisplay;

        TestCase(int[] sequence, boolean ampmState, int numberKeysEnabledStart, int numberKeysEnabledEnd, boolean backspaceEnabled, boolean headerDisplayFocused, boolean leftAltKeyEnabled, boolean rightAltKeyEnabled, boolean okButtonEnabled, CharSequence timeDisplay, CharSequence ampmDisplay) {
            this.sequence = sequence;
            this.ampmState = ampmState;
            this.numberKeysEnabledStart = numberKeysEnabledStart;
            this.numberKeysEnabledEnd = numberKeysEnabledEnd;
            this.backspaceEnabled = backspaceEnabled;
            this.headerDisplayFocused = headerDisplayFocused;
            this.leftAltKeyEnabled = leftAltKeyEnabled;
            this.rightAltKeyEnabled = rightAltKeyEnabled;
            this.okButtonEnabled = okButtonEnabled;
            this.timeDisplay = timeDisplay;
            this.ampmDisplay = ampmDisplay;
        }

        static class Builder {
            private final int[] sequence;
            private final boolean ampmState;

            private int numberKeysEnabledStart;
            private int numberKeysEnabledEnd;
            private boolean backspaceEnabled;
            private boolean headerDisplayFocused;
            private boolean leftAltKeyEnabled;
            private boolean rightAltKeyEnabled;
            private boolean okButtonEnabled;
            private CharSequence timeDisplay;
            private CharSequence ampmDisplay;

            public Builder(int[] sequence, boolean ampmState) {
                this.sequence = sequence;
                this.ampmState = ampmState;
            }

            public Builder numberKeysEnabled(int numberKeysEnabledStart, int numberKeysEnabledEnd) {
                this.numberKeysEnabledStart = numberKeysEnabledStart;
                this.numberKeysEnabledEnd = numberKeysEnabledEnd;
                return this;
            }

            public Builder backspaceEnabled(boolean backspaceEnabled) {
                this.backspaceEnabled = backspaceEnabled;
                return this;
            }

            public Builder altKeysEnabled(boolean enabled) {
                leftAltKeyEnabled = rightAltKeyEnabled = enabled;
                return this;
            }

            public Builder headerDisplayFocused(boolean headerDisplayFocused) {
                this.headerDisplayFocused = headerDisplayFocused;
                return this;
            }

            public Builder timeDisplay(CharSequence timeDisplay) {
                this.timeDisplay = timeDisplay;
                return this;
            }

            public Builder ampmDisplay(CharSequence ampmDisplay) {
                this.ampmDisplay = ampmDisplay;
                return this;
            }

            public Builder okButtonEnabled(boolean okButtonEnabled) {
                this.okButtonEnabled = okButtonEnabled;
                return this;
            }

            public TestCase build() {
                return new TestCase(sequence, ampmState, numberKeysEnabledStart, numberKeysEnabledEnd,
                        backspaceEnabled, headerDisplayFocused, leftAltKeyEnabled,
                        rightAltKeyEnabled, okButtonEnabled, timeDisplay, ampmDisplay);
            }
        }
    }
}
