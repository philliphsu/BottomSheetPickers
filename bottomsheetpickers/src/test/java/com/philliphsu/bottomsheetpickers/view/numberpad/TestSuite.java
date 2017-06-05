package com.philliphsu.bottomsheetpickers.view.numberpad;

import java.util.ArrayList;
import java.util.List;

import static com.philliphsu.bottomsheetpickers.view.numberpad.AmPmState.UNSPECIFIED;
import static com.philliphsu.bottomsheetpickers.view.numberpad.ButtonTextModel.text;

/*
 * TODO
 * #########
 * 24 hour
 * #########
 * 00-23 : 00-59 => 24 * 60 = 1440 tests
 * 0-9 : 00-59   => 10 * 60 =  600 tests
 *
 * #########
 * 12 hour
 * #########
 * 1-9 : 00-59 [am/pm]  => 9 * 60 * 2 = 1080 tests
 * 10-12 : 00-59 [am/pm] => 3 * 60 * 2 = 360 tests
 */
final class TestSuite {
    static final List<TestCase> MODE_12HR_TESTS_1_TO_9 = new ArrayList<>(9);
    static final List<TestCase> MODE_24HR_TESTS_0_TO_9 = new ArrayList<>(10);
    static final List<TestCase> MODE_12HR_TESTS_10_TO_95 = new ArrayList<>(54);

    static {
        build_Mode12Hr_Tests_1_to_9();
        build_Mode24Hr_Tests_0_to_9();
        build_Mode12Hr_Tests_10_to_95();
    }

    private static void build_Mode12Hr_Tests_1_to_9() {
        for (int i = 1; i <= 9; i++) {
            MODE_12HR_TESTS_1_TO_9.add(new TestCase.Builder(array(i), UNSPECIFIED)
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
            TestCase.Builder builder = new TestCase.Builder(array(i), UNSPECIFIED)
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
            TestCase test = new TestCase.Builder(array(i / 10, i % 10), UNSPECIFIED)
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

    private static int[] array(int... a) {
        return a == null ? new int[0] : a;
    }
}
