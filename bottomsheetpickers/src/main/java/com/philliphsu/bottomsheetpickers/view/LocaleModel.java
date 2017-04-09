package com.philliphsu.bottomsheetpickers.view;

import android.content.Context;
import android.os.Build;
import android.view.View;

/**
 * Model that encapsulates the formatting conventions of the user's locale.
 */
public final class LocaleModel {
    // TODO: The current utility APIs use this Context only to retrieve
    // the user's locale. Consider holding a reference to the Locale
    // instead of the Context, and passing the former to the utility class.
    private final Context mAppContext;

    public LocaleModel(Context context) {
        mAppContext = context.getApplicationContext();
    }

    public String getTimeSeparator(boolean is24Hour) {
        return DateTimeFormatUtils.getTimeSeparator(mAppContext, is24Hour);
    }

    public boolean isAmPmWrittenBeforeTime() {
        return DateTimeFormatUtils.isAmPmWrittenBeforeTime(mAppContext);
    }

    public boolean isLayoutRtl() {
        if (Build.VERSION.SDK_INT >= 17) {
            return mAppContext.getResources().getConfiguration()
                    .getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
        } else {
            // There is only LTR before SDK 17.
            return false;
        }
    }
}