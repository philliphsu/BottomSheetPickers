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

import com.philliphsu.clock2.R;

/**
 * Created by Phillip Hsu on 8/17/2016.
 */
public class HoursGrid extends NumbersGrid {

    public HoursGrid(Context context) {
        super(context);
    }

    @Override
    public void setSelection(int value) {
        super.setSelection(value);
        // We expect value to be within [1, 12]. The position in the grid where
        // value is located is thus (value - 1).
        setIndicator(getChildAt(value - 1));
    }

    @Override
    protected int contentLayout() {
        return R.layout.content_hours_grid;
    }

    @Override
    protected int indexOfDefaultValue() {
        // This is the index of number 12.
        return getChildCount() - 1;
    }
}
