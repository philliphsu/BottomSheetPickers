/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.philliphsu.bottomsheetpickers.time;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ConfigurationHelper;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.philliphsu.bottomsheetpickers.R;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Borrowed from AOSP Calculator's <code>CalculatorNumericPadLayout</code> class.
 *
 * @see <a href="https://android.googlesource.com/platform/packages/apps/Calculator/+/master/src/com/android/calculator2/CalculatorNumericPadLayout.java">CalculatorNumericPadLayout.java</a>
 */
public class NumberPadLayout extends BaseNumberPadLayout {

    public NumberPadLayout(Context context) {
        this(context, null);
    }

    public NumberPadLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberPadLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();

        Locale locale;
        final Configuration config = getResources().getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = config.getLocales().get(0);
        } else {
            // TODO: No backward-compatible method in the support libraries?
            //noinspection deprecation
            locale = config.locale;
        }
        if (!getResources().getBoolean(R.bool.use_localized_digits)
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // TODO: Do we need something similar for pre-21?
            locale = new Locale.Builder()
                .setLocale(locale)
                .setUnicodeLocaleKeyword("nu", "latn")
                .build();
        }

        final DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(locale);
        final char zeroDigit = symbols.getZeroDigit();
        for (int childIndex = getChildCount() - 1; childIndex >= 0; --childIndex) {
            final View v = getChildAt(childIndex);
            if (v instanceof Button) {
                final Button b = (Button) v;
                switch (b.getId()) {
                    case R.id.digit_0:
                        b.setText(String.valueOf(zeroDigit));
                        break;
                    case R.id.digit_1:
                        b.setText(String.valueOf((char) (zeroDigit + 1)));
                        break;
                    case R.id.digit_2:
                        b.setText(String.valueOf((char) (zeroDigit + 2)));
                        break;
                    case R.id.digit_3:
                        b.setText(String.valueOf((char) (zeroDigit + 3)));
                        break;
                    case R.id.digit_4:
                        b.setText(String.valueOf((char) (zeroDigit + 4)));
                        break;
                    case R.id.digit_5:
                        b.setText(String.valueOf((char) (zeroDigit + 5)));
                        break;
                    case R.id.digit_6:
                        b.setText(String.valueOf((char) (zeroDigit + 6)));
                        break;
                    case R.id.digit_7:
                        b.setText(String.valueOf((char) (zeroDigit + 7)));
                        break;
                    case R.id.digit_8:
                        b.setText(String.valueOf((char) (zeroDigit + 8)));
                        break;
                    case R.id.digit_9:
                        b.setText(String.valueOf((char) (zeroDigit + 9)));
                        break;
                    case R.id.dec_point:
                        b.setText(String.valueOf(symbols.getDecimalSeparator()));
                        break;
                }
            }
        }
    }
}

