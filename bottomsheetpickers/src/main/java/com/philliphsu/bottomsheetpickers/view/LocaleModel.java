package com.philliphsu.bottomsheetpickers.view;

import android.content.Context;

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
}
