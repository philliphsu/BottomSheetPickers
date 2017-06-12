package com.philliphsu.bottomsheetpickers.time.numberpad;

import android.content.Context;
import android.os.Build;
import android.view.View;

/**
 * Model that encapsulates the formatting conventions of the user's locale.
 */
class LocaleModel {
    // TODO: The current utility APIs use this Context only to retrieve
    // the user's locale. Consider holding a reference to the Locale
    // instead of the Context, and passing the former to the utility class.
    private final Context mAppContext;

    LocaleModel(Context context) {
        mAppContext = context.getApplicationContext();
    }

    String getTimeSeparator(boolean is24Hour) {
        return DateTimeFormatUtils.getTimeSeparator(mAppContext, is24Hour);
    }

    boolean isAmPmWrittenBeforeTime() {
        return DateTimeFormatUtils.isAmPmWrittenBeforeTime(mAppContext);
    }

    boolean isLayoutRtl() {
        if (Build.VERSION.SDK_INT >= 17) {
            return mAppContext.getResources().getConfiguration()
                    .getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
        } else {
            // There is only LTR before SDK 17.
            return false;
        }
    }
}