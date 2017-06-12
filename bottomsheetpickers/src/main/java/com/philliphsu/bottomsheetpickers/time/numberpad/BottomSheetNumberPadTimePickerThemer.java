package com.philliphsu.bottomsheetpickers.time.numberpad;

import android.content.res.ColorStateList;
import android.support.annotation.ColorInt;

/**
 * Adds {@link NumberPadTimePicker#LAYOUT_BOTTOM_SHEET bottom sheet} theming APIs
 * to the {@link NumberPadTimePickerThemer base set of APIs}.
 */
interface BottomSheetNumberPadTimePickerThemer extends NumberPadTimePickerThemer {
    BottomSheetNumberPadTimePickerThemer setFabBackgroundColor(ColorStateList colors);

    BottomSheetNumberPadTimePickerThemer setFabRippleColor(@ColorInt int color);

    BottomSheetNumberPadTimePickerThemer setFabIconTint(ColorStateList tint);

    BottomSheetNumberPadTimePickerThemer setAnimateFabBackgroundColor(boolean animate);

    BottomSheetNumberPadTimePickerThemer setShowFabPolicy(@ShowFabPolicy int policy);

    BottomSheetNumberPadTimePickerThemer setAnimateFabIn(boolean animateIn);

    BottomSheetNumberPadTimePickerThemer setBackspaceLocation(@BackspaceLocation int location);
}
