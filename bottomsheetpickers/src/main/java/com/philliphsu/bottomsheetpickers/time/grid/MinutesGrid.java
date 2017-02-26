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
import android.widget.ImageButton;

import com.philliphsu.bottomsheetpickers.R;
import com.philliphsu.bottomsheetpickers.Utils;

public class MinutesGrid extends NumbersGrid {
    private static final String TAG = "MinutesGrid";

    private ImageButton mMinusButton;
    private ImageButton mPlusButton;

    public MinutesGrid(Context context) {
        super(context);
    }

    public MinutesGrid(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MinutesGrid(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mMinusButton = (ImageButton) findViewById(R.id.bsp_dec_min);
        mPlusButton = (ImageButton) findViewById(R.id.bsp_inc_min);
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
    void setTheme(Context context, boolean themeDark) {
        super.setTheme(context, themeDark);
        if (!themeDark) {
            final int colorActiveLight = ContextCompat.getColor(context, R.color.bsp_icon_color_active_light);
            Utils.applyTint(mMinusButton, colorActiveLight);
            Utils.applyTint(mPlusButton, colorActiveLight);
        }
    }

}
