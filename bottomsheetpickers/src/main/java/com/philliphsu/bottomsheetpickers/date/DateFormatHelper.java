package com.philliphsu.bottomsheetpickers.date;

import android.text.format.DateUtils;

import java.util.Calendar;
import java.util.Formatter;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Provides a simple API to format a date.
 */
final class DateFormatHelper {
    /* Use these to save on object instantiations done internally by DateUtils. */
    private static final StringBuilder STRINGBUILDER = new StringBuilder(50);
    private static final Formatter FORMATTER = new Formatter(STRINGBUILDER, Locale.getDefault());

    static String formatDate(final Calendar calendar, final int flags) {
        return formatDate(calendar.getTimeInMillis(), flags);
    }

    static String formatDate(final long millis, final int flags) {
        STRINGBUILDER.setLength(0);
        // We don't require a Context because we're not including the time.
        return DateUtils.formatDateRange(null, FORMATTER, millis, millis, flags,
                TimeZone.getDefault().getID()).toString();
    }
}
