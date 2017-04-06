package com.philliphsu.bottomsheetpickers.view;

import android.content.Context;

/**
 * Model that encapsulates the formatting conventions of the user's locale.
 */
public final class LocaleModel {
    private final Context mAppContext;

    public LocaleModel(Context context) {
        mAppContext = context.getApplicationContext();
    }

    public String getTimeSeparator(boolean is24Hour) {
        return DateTimeFormatUtils.getTimeSeparator(mAppContext, is24Hour);
    }
}
