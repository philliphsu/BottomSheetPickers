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

package com.philliphsu.bottomsheetpickers.time.grid;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.philliphsu.bottomsheetpickers.R;

public class TwentyFourHourGridItem extends LinearLayout {

    private final TextView mPrimaryText;
    private final TextView mSecondaryText;

    public TwentyFourHourGridItem(Context context) {
        this(context, null);
    }

    public TwentyFourHourGridItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        final int orientation = context.getResources().getConfiguration().orientation;
        setOrientation(orientation == Configuration.ORIENTATION_PORTRAIT ? VERTICAL : HORIZONTAL);
        setGravity(Gravity.CENTER);
        inflate(getContext(), R.layout.content_24h_grid_item, this);

        mPrimaryText = (TextView) findViewById(R.id.primary);
        mSecondaryText = (TextView) findViewById(R.id.secondary);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.TwentyFourHourGridItem, 0, 0);
        try {
            setPrimaryText(a.getString(R.styleable.TwentyFourHourGridItem_primaryText));
            setSecondaryText(a.getString(R.styleable.TwentyFourHourGridItem_secondaryText));
        } finally {
            a.recycle();
        }
    }

    public CharSequence getPrimaryText() {
        return mPrimaryText.getText();
    }

    public void setPrimaryText(CharSequence text) {
        mPrimaryText.setText(text);
    }

    public CharSequence getSecondaryText() {
        return mSecondaryText.getText();
    }

    public void setSecondaryText(CharSequence text) {
        mSecondaryText.setText(text);
    }

    public void swapTexts() {
        CharSequence primary = mPrimaryText.getText();
        setPrimaryText(mSecondaryText.getText());
        setSecondaryText(primary);
    }

    public TextView getPrimaryTextView() {
        return (TextView) getChildAt(0);
    }

    public TextView getSecondaryTextView() {
        return (TextView) getChildAt(1);
    }
}
