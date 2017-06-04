package com.philliphsu.bottomsheetpickers.view.numberpad;

import android.content.res.ColorStateList;
import android.support.annotation.NonNull;

/**
 * Adds {@link NumberPadTimePicker#LAYOUT_BOTTOM_SHEET bottom sheet} theming APIs
 * to the {@link NumberPadTimePickerThemer base set of APIs}.
 */
public class BottomSheetNumberPadTimePickerDialogThemer extends NumberPadTimePickerDialogThemer {

    BottomSheetNumberPadTimePickerDialogThemer(@NonNull NumberPadTimePicker timePicker) {
        super(timePicker);
    }

    public void setFabBackgroundColor(ColorStateList colors) {
        ((NumberPadTimePickerBottomSheetComponent) mTimePicker.getComponent())
                .setFabBackgroundColor(colors);
    }
}
