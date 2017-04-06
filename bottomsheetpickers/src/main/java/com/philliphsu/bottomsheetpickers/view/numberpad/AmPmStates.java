package com.philliphsu.bottomsheetpickers.view.numberpad;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Defines the {@link AmPmState} annotation. Provides constants
 * to help determine how time should be formatted for display.
 */
final class AmPmStates {
    private AmPmStates() {}

    static final int UNSPECIFIED = -1;
    static final int AM = 0;
    static final int PM = 1;
    static final int HRS_24 = 2;

    @IntDef({UNSPECIFIED, AM, PM, HRS_24})
    @Retention(RetentionPolicy.SOURCE)
    @interface AmPmState {}
}
