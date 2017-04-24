package com.philliphsu.bottomsheetpickers.view.numberpad;

import java.util.ArrayList;
import java.util.List;

import static com.philliphsu.bottomsheetpickers.view.numberpad.AmPmStates.UNSPECIFIED;
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

    static {
        // ####################################################################
        //                     MODE_12HR: Input '1' - '9'
        // ####################################################################
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

        // ####################################################################
        //                     MODE_24HR: Input '0' - '9'
        // ####################################################################
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

    private static int[] array(int... a) {
        return a == null ? new int[0] : a;
    }
}
