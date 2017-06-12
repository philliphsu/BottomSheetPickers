package com.philliphsu.bottomsheetpickers.time.numberpad;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.philliphsu.bottomsheetpickers.time.numberpad.ShowFabPolicy.SHOW_FAB_ALWAYS;
import static com.philliphsu.bottomsheetpickers.time.numberpad.ShowFabPolicy.SHOW_FAB_VALID_TIME;

/**
 * Options to set when the floating action button in a {@link BottomSheetNumberPadTimePickerDialog}
 * should be shown.
 */
@IntDef({SHOW_FAB_ALWAYS, SHOW_FAB_VALID_TIME})
@Retention(RetentionPolicy.SOURCE)
@interface ShowFabPolicy {
    /** Option to always show the FAB. */
    int SHOW_FAB_ALWAYS = 0;
    /** Option to only show the FAB when the inputted sequence makes a valid time. */
    int SHOW_FAB_VALID_TIME = 1;
}
