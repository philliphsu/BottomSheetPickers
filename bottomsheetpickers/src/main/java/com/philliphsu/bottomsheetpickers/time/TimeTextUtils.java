/*
 * Copyright (C) 2016 Phillip Hsu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.philliphsu.bottomsheetpickers.time;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.widget.TextView;

public class TimeTextUtils {

    private TimeTextUtils() {}

    private static final RelativeSizeSpan AMPM_SIZE_SPAN = new RelativeSizeSpan(0.5f);

    /**
     * Sets the given String on the TextView.
     * If the given String contains the "AM" or "PM" label,
     * this first applies a size span on the label.
     * @param textTime the time String that may contain "AM" or "PM"
     * @param textView the TextView to display {@code textTime}
     */
    public static void setText(String textTime, TextView textView) {
        // TODO: This is not localized. Get the AM/PM translation from DateFormatSymbols.
        if (textTime.contains("AM") || textTime.contains("PM")) {
            SpannableString s = new SpannableString(textTime);
            s.setSpan(AMPM_SIZE_SPAN, textTime.indexOf(" "), textTime.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setText(s, TextView.BufferType.SPANNABLE);
        } else {
            textView.setText(textTime);
        }
    }
}
