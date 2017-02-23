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

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.philliphsu.bottomsheetpickers.BottomSheetPickerDialog;

public abstract class BottomSheetTimePickerDialog extends BottomSheetPickerDialog {
    private static final String TAG = "BottomSheetTimePickerDialog";

    private OnTimeSetListener mCallback;

    /**
     * The callback interface used to indicate the user is done filling in
     * the time (they clicked on the 'Set' button).
     */
    public interface OnTimeSetListener {
        /**
         * @param viewGroup The view associated with this listener.
         * @param hourOfDay The hour that was set.
         * @param minute The minute that was set.
         */
        void onTimeSet(ViewGroup viewGroup, int hourOfDay, int minute);
    }

    public final void setOnTimeSetListener(OnTimeSetListener callback) {
        mCallback = callback;
    }

    protected final void onTimeSet(ViewGroup vg, int hourOfDay, int minute) {
        if (mCallback != null) {
            mCallback.onTimeSet(vg, hourOfDay, minute);
        }
        dismiss();
    }

    protected static abstract class Builder extends BottomSheetPickerDialog.Builder {
        protected final OnTimeSetListener mListener;
        protected final boolean mIs24HourMode;

        protected Builder(OnTimeSetListener listener) {
            this(listener, false);
        }

        protected Builder(OnTimeSetListener listener, boolean is24HourMode) {
            mListener = listener;
            mIs24HourMode = is24HourMode;
        }

        @Override
        protected final void super_build(@NonNull BottomSheetPickerDialog dialog) {
            super.super_build(dialog);
        }
    }
}
