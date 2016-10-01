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

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * Created by Phillip Hsu on 7/16/2016.
 */
public abstract class BaseTimePickerDialog extends BottomSheetDialogFragment {
    private static final String TAG = "BaseTimePickerDialog";

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
        // TODO: Consider removing VG param, since listeners probably won't need to use it....
        void onTimeSet(ViewGroup viewGroup, int hourOfDay, int minute);
    }

    /**
     * Empty constructor required for dialog fragment.
     * Subclasses do not need to write their own.
     */
    public BaseTimePickerDialog() {}

    @LayoutRes
    protected abstract int contentLayout();

    public final void setOnTimeSetListener(OnTimeSetListener callback) {
        mCallback = callback;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Not needed for bottom sheet dialogs
//        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        final View view = inflater.inflate(contentLayout(), container, false);
        ButterKnife.bind(this, view);

        // TODO: We could move this to onCreateDialog() if we cared.
        //
        // onShow() is called immediately as this DialogFragment is showing, so the
        // FAB's animation will barely be noticeable.
//        getDialog().setOnShowListener(new DialogInterface.OnShowListener() {
//            @Override
//            public void onShow(DialogInterface dialog) {
//                Log.i(TAG, "onShow()");
//                // Animate the FAB into view
//                View v = view.findViewById(R.id.fab);
//                if (v != null) {
//                    FloatingActionButton fab = (FloatingActionButton) v;
//                    fab.show();
//                }
//            }
//        });

        return view;
    }

    protected final void onTimeSet(ViewGroup vg, int hourOfDay, int minute) {
        if (mCallback != null) {
            mCallback.onTimeSet(vg, hourOfDay, minute);
        }
        dismiss();
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        final View view = getView();
//        final BottomSheetBehavior behavior = BottomSheetBehavior.from((View) view.getParent());
//        // Copy over the internal callback logic, and also implement our own
//        //
//        // This callback is set AFTER this Fragment has become visible, so is useless for what
//        // you wanted to do (show the FAB during the settling phase).
//        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
//            @Override
//            public void onStateChanged(@NonNull View bottomSheet, int newState) {
//                Log.i(TAG, "onStateChanged(): " + newState);
//                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
//                    dismiss();
//                }
//                // My logic below
//                else if (newState == BottomSheetBehavior.STATE_SETTLING) {
//                    View fab = view.findViewById(R.id.fab);
//                    if (fab != null) {
//                        ((FloatingActionButton) fab).show();
//                    }
//                }
//            }
//
//            @Override
//            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//
//            }
//        });
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    // Code for AlertDialog style only.
//    @NonNull
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        // Use an AlertDialog to display footer buttons, rather than
//        // re-invent them in our layout.
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setView(contentLayout())
//                // The action strings are already defined and localized by the system!
//                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                })
//                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                });
//        return builder.create();
//    }

    // This was an unsatisfactory solution to forcing the bottom sheet to show at its
    // fully expanded state. Our anchored FAB and GridLayout buttons would not be visible.
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        Dialog dialog = super.onCreateDialog(savedInstanceState);
//        //dialog = new BottomSheetDialog(getActivity(), R.style.AppTheme_AppCompatDialog/*crashes our app!*/);
//        // We're past onCreate() in the lifecycle, so the activity is alive.
//        View view = LayoutInflater.from(getActivity()).inflate(contentLayout(), null);
//        /**
//         * Adds our view to a ViewGroup that has a BottomSheetBehavior attached. The ViewGroup
//         * itself is a child of a CoordinatorLayout.
//         * @see {@link BottomSheetDialog#wrapInBottomSheet(int, View, ViewGroup.LayoutParams)}
//         */
//        dialog.setContentView(view);
//        // Bind this fragment, not the internal dialog! (There is a bind(Dialog) API.)
//        ButterKnife.bind(this, view);
//        final BottomSheetBehavior behavior = BottomSheetBehavior.from((View) view.getParent());

//        // When we collapse, collapse all the way. Do not be misled by the "docs" in
//        // https://android-developers.blogspot.com.au/2016/02/android-support-library-232.html
//        // when it says:
//        // "STATE_COLLAPSED: ... the app:behavior_peekHeight attribute (defaults to 0)"
//        // While it is true by default, BottomSheetDialogs override this default height.

          // This means the sheet is considered "open" even at a height of 0! This is why
//        // when you swipe to hide the sheet, the screen remains darkened--indicative
//        // of an open dialog.
//        behavior.setPeekHeight(0);

//        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
//            @Override
//            public void onShow(DialogInterface dialog) {
//                // Every time we show, show at our full height.
//                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//            }
//        });
//
//        return dialog;
//    }
}
