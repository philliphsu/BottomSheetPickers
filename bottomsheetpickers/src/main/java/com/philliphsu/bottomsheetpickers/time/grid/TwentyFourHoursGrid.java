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
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.philliphsu.bottomsheetpickers.R;
import com.philliphsu.bottomsheetpickers.Utils;

public class TwentyFourHoursGrid extends NumbersGrid implements View.OnLongClickListener {
    private static final String TAG = "TwentyFourHoursGrid";

    private int mSecondaryTextColor;

    public TwentyFourHoursGrid(Context context) {
        this(context, null);
    }

    public TwentyFourHoursGrid(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TwentyFourHoursGrid(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mSecondaryTextColor = ContextCompat.getColor(context, R.color.bsp_text_color_secondary_light);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setOnLongClickListener(this);
        }
    }

    @Override
    protected boolean canRegisterClickListener(View view) {
        return view instanceof TwentyFourHourGridItem;
    }

    @Override
    public void onClick(View v) {
        final int newVal = valueOf(v);
        setSelection(newVal);
        mSelectionListener.onNumberSelected(newVal);
    }

    @Override
    public boolean onLongClick(View v) {
        TwentyFourHourGridItem item = (TwentyFourHourGridItem) v;
        // Unfortunately, we can't use #valueOf() for this because we want the secondary value.
        int newVal = Integer.parseInt(item.getSecondaryText().toString());
        mSelectionListener.onNumberSelected(newVal);
        // TOneverDO: Call before firing the onNumberSelected() callback, because we want the
        // dialog to advance to the next index WITHOUT seeing the text swapping.
        swapTexts();
        // TOneverDO: Call before swapping texts, because setIndicator() uses the primary TextView.
        setSelection(newVal);
        return true; // Consume the long click
    }

    @Override
    public void setSelection(int value) {
        super.setSelection(value);
        // The value is within [0, 23], but we have only 12 buttons.
        setIndicator(getChildAt(value % 12));
    }

    @Override
    protected void setIndicator(View view) {
        TwentyFourHourGridItem item = (TwentyFourHourGridItem) view;
        super.setIndicator(item.getPrimaryTextView());
    }

    @Override
    void setTheme(Context context, boolean themeDark) {
        mDefaultTextColor = ContextCompat.getColor(context, themeDark?
                R.color.bsp_text_color_primary_dark : R.color.bsp_text_color_primary_light);
        mSecondaryTextColor = ContextCompat.getColor(context, themeDark?
                R.color.bsp_text_color_secondary_dark : R.color.bsp_text_color_secondary_light);
        for (int i = 0; i < getChildCount(); i++) {
            TwentyFourHourGridItem item = (TwentyFourHourGridItem) getChildAt(i);
            // TODO: We could move this to the ctor, in the superclass. If so, then this class
            // doesn't need to worry about setting the highlight.
            Utils.setColorControlHighlight(item, mSelectedTextColor/*colorAccent*/);
            // Filter out the current selection.
            if (getSelection() != valueOf(item)) {
                item.getPrimaryTextView().setTextColor(mDefaultTextColor);
                // The indicator can only be set on the primary text, which is why we don't need
                // the secondary text here.
            }
            item.getSecondaryTextView().setTextColor(mSecondaryTextColor);
        }
    }

    public void swapTexts() {
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            ((TwentyFourHourGridItem) v).swapTexts();
        }
    }

    @Override
    protected int valueOf(View button) {
        return Integer.parseInt(((TwentyFourHourGridItem) button).getPrimaryText().toString());
    }
}