package com.philliphsu.bottomsheetpickers.time.numberpad;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.philliphsu.bottomsheetpickers.time.numberpad.BackspaceLocation.LOCATION_FOOTER;
import static com.philliphsu.bottomsheetpickers.time.numberpad.BackspaceLocation.LOCATION_HEADER;

/**
 * Options to set the location of the backspace button in a
 * {@link BottomSheetNumberPadTimePickerDialog}.
 */
@IntDef({LOCATION_HEADER, LOCATION_FOOTER})
@Retention(RetentionPolicy.SOURCE)
@interface BackspaceLocation {
    /** Option to place the backspace button in the header. */
    int LOCATION_HEADER = 0;
    /** Option to place the backspace button in the footer. */
    int LOCATION_FOOTER = 1;
}
