package com.philliphsu.bottomsheetpickers.time.numberpad;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.philliphsu.bottomsheetpickers.time.numberpad.AmPmState.AM;
import static com.philliphsu.bottomsheetpickers.time.numberpad.AmPmState.HRS_24;
import static com.philliphsu.bottomsheetpickers.time.numberpad.AmPmState.PM;
import static com.philliphsu.bottomsheetpickers.time.numberpad.AmPmState.UNSPECIFIED;

/**
 * Constants to determine how time should be formatted for display.
 */
@IntDef({UNSPECIFIED, AM, PM, HRS_24})
@Retention(RetentionPolicy.SOURCE)
@interface AmPmState {
    int UNSPECIFIED = -1;
    int AM = 0;
    int PM = 1;
    int HRS_24 = 2;  // a.k.a. NOT_APPLICABLE
}
