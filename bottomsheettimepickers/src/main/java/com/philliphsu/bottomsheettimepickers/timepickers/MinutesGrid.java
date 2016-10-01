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

package com.philliphsu.bottomsheettimepickers.timepickers;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageButton;

import com.philliphsu.bottomsheettimepickers.R;

/**
 * Created by Phillip Hsu on 8/17/2016.
 */
public class MinutesGrid extends NumbersGrid {
    private static final String TAG = "MinutesGrid";

    private final ImageButton mMinusButton;
    private final ImageButton mPlusButton;

    public MinutesGrid(Context context) {
        super(context);
        mMinusButton = (ImageButton) getChildAt(getChildCount() - 2);
        mPlusButton = (ImageButton) getChildAt(getChildCount() - 1);
        // We're not doing method binding because we don't have IDs set on these buttons.
        mMinusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value = getSelection() - 1;
                if (value < 0)
                    value = 59;
                setSelection(value);
                mSelectionListener.onNumberSelected(value);
            }
        });
        mPlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value = getSelection() + 1;
                if (value == 60)
                    value = 0;
                setSelection(value);
                mSelectionListener.onNumberSelected(value);
            }
        });
    }

    @Override
    public void setSelection(int value) {
        super.setSelection(value);
        if (value % 5 == 0) {
            // The new value is one of the predetermined minute values
            int positionOfValue = value / 5;
            setIndicator(getChildAt(positionOfValue));
        } else {
            clearIndicator();
        }
    }

    @Override
    protected int contentLayout() {
        return R.layout.content_minutes_grid;
    }

    @Override
    void setTheme(Context context, boolean themeDark) {
        super.setTheme(context, themeDark);
        if (themeDark) {
            // Resources default to dark-themed color (#FFFFFF)
            // If vector fill color is transparent, programmatically tinting will not work.
            // Since dark-themed active icon color is fully opaque, use that color as the
            // base color and tint at runtime as needed.
            mMinusButton.setImageResource(R.drawable.ic_minus_circle_24dp);
            mPlusButton.setImageResource(R.drawable.ic_add_circle_24dp);
        } else {
            // Tint drawables
            final int colorActiveLight = ContextCompat.getColor(context, R.color.icon_color_active_light);
            mMinusButton.setImageDrawable(Utils.getTintedDrawable(
                    context, R.drawable.ic_minus_circle_24dp, colorActiveLight));
            mPlusButton.setImageDrawable(Utils.getTintedDrawable(
                    context, R.drawable.ic_add_circle_24dp, colorActiveLight));
        }
    }
}
