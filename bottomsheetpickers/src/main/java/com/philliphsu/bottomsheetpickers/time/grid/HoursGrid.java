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
import android.util.AttributeSet;

public class HoursGrid extends NumbersGrid {

    public HoursGrid(Context context) {
        super(context);
    }

    public HoursGrid(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HoursGrid(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setSelection(int value) {
        super.setSelection(value);
        // We expect value to be within [1, 12]. The position in the grid where
        // value is located is thus (value - 1).
        setIndicator(getChildAt(value - 1));
    }

    @Override
    protected int indexOfDefaultValue() {
        // This is the index of number 12.
        return getChildCount() - 1;
    }
}
